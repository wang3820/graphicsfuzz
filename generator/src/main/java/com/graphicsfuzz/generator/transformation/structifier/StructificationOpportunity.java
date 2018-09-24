/*
 * Copyright 2018 The GraphicsFuzz Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.graphicsfuzz.generator.transformation.structifier;

import com.graphicsfuzz.common.ast.IParentMap;
import com.graphicsfuzz.common.ast.TranslationUnit;
import com.graphicsfuzz.common.ast.decl.ScalarInitializer;
import com.graphicsfuzz.common.ast.decl.StructDeclaration;
import com.graphicsfuzz.common.ast.decl.VariableDeclInfo;
import com.graphicsfuzz.common.ast.expr.Expr;
import com.graphicsfuzz.common.ast.expr.MemberLookupExpr;
import com.graphicsfuzz.common.ast.expr.TypeConstructorExpr;
import com.graphicsfuzz.common.ast.expr.VariableIdentifierExpr;
import com.graphicsfuzz.common.ast.stmt.BlockStmt;
import com.graphicsfuzz.common.ast.stmt.DeclarationStmt;
import com.graphicsfuzz.common.ast.type.BasicType;
import com.graphicsfuzz.common.ast.type.StructType;
import com.graphicsfuzz.common.ast.type.Type;
import com.graphicsfuzz.common.glslversion.ShadingLanguageVersion;
import com.graphicsfuzz.common.transformreduce.Constants;
import com.graphicsfuzz.common.typing.ScopeEntry;
import com.graphicsfuzz.common.typing.ScopeTreeBuilder;
import com.graphicsfuzz.common.typing.SupportedTypes;
import com.graphicsfuzz.common.util.IRandom;
import com.graphicsfuzz.common.util.IdGenerator;
import com.graphicsfuzz.generator.util.GenerationParams;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StructificationOpportunity {

  private final DeclarationStmt declToTransform; // The declaration to be put into a struct,
  // which must be a single declaration.

  private final BlockStmt block; // The block in which the declaration resides.

  private final TranslationUnit tu; // The enclosing translation unit, into which new struct
  // declarations will be inserted.

  private final ShadingLanguageVersion shadingLanguageVersion;

  public StructificationOpportunity(DeclarationStmt declToTransform, BlockStmt block,
        TranslationUnit tu, ShadingLanguageVersion shadingLanguageVersion) {
    assert declToTransform.getVariablesDeclaration().getNumDecls() == 1 :
          "Only solo declarations can be structified";
    assert !declToTransform.getVariablesDeclaration().getDeclInfo(0).hasArrayInfo() :
          "Structification of arrays not supported";
    this.declToTransform = declToTransform;
    this.block = block;
    this.tu = tu;
    this.shadingLanguageVersion = shadingLanguageVersion;
  }

  /**
   * Creates a random struct, returned as the first declaration in the result list.  The struct
   * might itself contain one or more sub-structs, and the declarations for all generated sub-
   * structs will be in the subsequent elements of the list.  If the declarations of the list
   * are printed in reverse order, so that the final generated struct (element 0 of the list)
   * appears last, then all structs will be declared before they are used.
   *
   * @param currentDepth The method is recursive, as sub-structs can be generated; indicates
   *                     recursion depth.
   * @param generator Used for random generation
   * @param idGenerator Facilitates creation of unique struct names.
   * @return List of struct declarations, with the first declaration being the final generated
   *         struct, and the following declarations all required sub-structs.
   */
  static List<StructDeclaration> randomStruct(int currentDepth, IRandom generator,
        IdGenerator idGenerator, ShadingLanguageVersion shadingLanguageVersion,
        GenerationParams generationParams) {

    // Choose how many fields to have.  We cannot have 0 fields in GLSL, hence the arithmetic.
    final int numFields = generator.nextInt(generationParams.getMaxStructFields() - 1) + 1;

    // Used to store sub-structs that are generated on-the-fly.
    List<StructDeclaration> subStructs = new ArrayList<>();

    // Field names and types of the new struct to be generated.
    List<String> fieldNames = new ArrayList<>();
    List<Type> fieldTypes = new ArrayList<>();

    // For each field...
    for (int i = 0; i < numFields; i++) {
      // ...give it a standard name.
      fieldNames.add(Constants.STRUCTIFICATION_FIELD_PREFIX + i);
      // Choose whether to add a struct or primitive field.
      if (currentDepth < generationParams.getMaxStructNestingDepth() && generator.nextBoolean()) {
        // We recursively generate a struct (with associated sub-structs).
        List<StructDeclaration> newStructs = randomStruct(currentDepth + 1, generator,
              idGenerator, shadingLanguageVersion, generationParams);
        // All these new structs are now available to us for further fields.
        subStructs.addAll(newStructs);
        // The first struct in the list is the type of the field we are currently generating.
        fieldTypes.add(newStructs.get(0).getType());
      } else {
        // Grab a basic type.
        while (true) {
          BasicType candidate = BasicType.allBasicTypes()
                .get(generator.nextInt(BasicType.allBasicTypes()
                      .size()));
          if (SupportedTypes.supported(candidate, shadingLanguageVersion)) {
            fieldTypes.add(candidate);
            break;
          }
        }
      }
    }

    // The result is the struct we have generated, followed by all the sub-structs gathered on the
    // way.
    List<StructDeclaration> result = new ArrayList<>();
    result.add(new StructDeclaration(new StructType(Constants.STRUCTIFICATION_STRUCT_PREFIX
          + idGenerator.freshId(), fieldNames, fieldTypes)));
    result.addAll(subStructs);
    return result;
  }

  public void apply(IdGenerator idGenerator, IRandom generator,
        GenerationParams generationParams) {
    List<StructDeclaration> generatedStructs = randomStruct(0, generator, idGenerator,
        shadingLanguageVersion, generationParams);

    generatedStructs.forEach(tu::addDeclaration);

    final String enclosingStructVariableName = Constants.GLF_STRUCT_REPLACEMENT
          + idGenerator.freshId();
    final StructType enclosingStructType = generatedStructs.get(0).getType();

    Expr structifiedExpr = insertFieldIntoStruct(
          enclosingStructVariableName, enclosingStructType, generator);

    // Rename all occurrences of the variable in the block.
    structifyBlock(structifiedExpr);
    // Then rename the variable at its declaration site.
    structifyDeclaration(enclosingStructVariableName, enclosingStructType);

  }

  private void structifyDeclaration(String enclosingStructVariableName,
        StructType enclosingStructType) {
    declToTransform.getVariablesDeclaration()
          .setBaseType(enclosingStructType);
    final VariableDeclInfo declInfo = declToTransform.getVariablesDeclaration().getDeclInfo(0);
    declInfo.setName(enclosingStructVariableName);
    if (declInfo.hasInitializer()) {
      declInfo.setInitializer(
            new ScalarInitializer(
                  makeInitializationExpr(enclosingStructType,
                        ((ScalarInitializer) declInfo.getInitializer()).getExpr())
            )
      );
    }
  }

  private Expr makeInitializationExpr(StructType structType, Expr originalInitializer) {
    List<Expr> args = new ArrayList<>();
    for (int i = 0; i < structType.getNumFields(); i++) {
      if (structType.getFieldName(i).startsWith(Constants.STRUCTIFICATION_FIELD_PREFIX)) {
        final Type fieldType = structType.getFieldType(i);
        args.add(fieldType instanceof StructType
              ? makeInitializationExpr((StructType) fieldType, originalInitializer)
              : fieldType.getCanonicalConstant());
      } else {
        args.add(originalInitializer);
      }
    }
    return new TypeConstructorExpr(structType.getName(),
          args);
  }

  private void structifyBlock(Expr structifiedExpr) {

    final IParentMap parentMap = IParentMap.createParentMap(block);

    new ScopeTreeBuilder() {
      @Override
      public void visitVariableIdentifierExpr(VariableIdentifierExpr variableIdentifierExpr) {
        super.visitVariableIdentifierExpr(variableIdentifierExpr);
        ScopeEntry se = currentScope.lookupScopeEntry(variableIdentifierExpr.getName());
        if (se == null) {
          // We are traversing a block in isolation, so we won't have a scope entry for any variable
          // declared outside the block.
          return;
        }
        if (se.getVariablesDeclaration() == declToTransform.getVariablesDeclaration()) {
          parentMap.getParent(variableIdentifierExpr).replaceChild(
                variableIdentifierExpr, structifiedExpr.clone());
        }
      }
    }.visit(block);

  }

  private Expr insertFieldIntoStruct(String enclosingStructName,
        StructType enclosingStructType, IRandom generator) {
    Expr result = new VariableIdentifierExpr(enclosingStructName);
    StructType currentStructType = enclosingStructType;
    while (true) {
      Map<String, StructType> structFields = getStructFields(currentStructType);
      if (!structFields.keySet().isEmpty() && generator.nextBoolean()) {
        String fieldName = structFields.keySet().stream().collect(Collectors.toList())
              .get(generator.nextInt(structFields.size()));
        result = new MemberLookupExpr(result, fieldName);
        currentStructType = structFields.get(fieldName);
      } else {
        // Choose random position at which to insert the field.
        currentStructType.insertField(generator.nextInt(currentStructType.getNumFields() + 1),
              declToTransform.getVariablesDeclaration().getDeclInfo(0).getName(),
              declToTransform.getVariablesDeclaration().getBaseType());
        result = new MemberLookupExpr(result, declToTransform.getVariablesDeclaration()
              .getDeclInfo(0).getName());
        return result;
      }
    }
  }

  private Map<String, StructType> getStructFields(StructType type) {
    return type.getFieldNames().stream()
          .filter(item -> type.getFieldType(item) instanceof StructType)
          .collect(Collectors.toMap(
              item -> item,
              item -> (StructType) type.getFieldType(item))
          );
  }

}
