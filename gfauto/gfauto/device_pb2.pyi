# @generated by generate_proto_mypy_stubs.py.  Do not edit!
import sys
from gfauto.common_pb2 import (
    Binary as gfauto___common_pb2___Binary,
)

from google.protobuf.descriptor import (
    Descriptor as google___protobuf___descriptor___Descriptor,
)

from google.protobuf.internal.containers import (
    RepeatedCompositeFieldContainer as google___protobuf___internal___containers___RepeatedCompositeFieldContainer,
    RepeatedScalarFieldContainer as google___protobuf___internal___containers___RepeatedScalarFieldContainer,
)

from google.protobuf.message import (
    Message as google___protobuf___message___Message,
)

from typing import (
    Iterable as typing___Iterable,
    Optional as typing___Optional,
    Text as typing___Text,
)

from typing_extensions import (
    Literal as typing_extensions___Literal,
)


builtin___bool = bool
builtin___bytes = bytes
builtin___float = float
builtin___int = int


class DeviceList(google___protobuf___message___Message):
    DESCRIPTOR: google___protobuf___descriptor___Descriptor = ...
    active_device_names = ... # type: google___protobuf___internal___containers___RepeatedScalarFieldContainer[typing___Text]

    @property
    def devices(self) -> google___protobuf___internal___containers___RepeatedCompositeFieldContainer[Device]: ...

    def __init__(self,
        *,
        active_device_names : typing___Optional[typing___Iterable[typing___Text]] = None,
        devices : typing___Optional[typing___Iterable[Device]] = None,
        ) -> None: ...
    @classmethod
    def FromString(cls, s: builtin___bytes) -> DeviceList: ...
    def MergeFrom(self, other_msg: google___protobuf___message___Message) -> None: ...
    def CopyFrom(self, other_msg: google___protobuf___message___Message) -> None: ...
    if sys.version_info >= (3,):
        def ClearField(self, field_name: typing_extensions___Literal[u"active_device_names",u"devices"]) -> None: ...
    else:
        def ClearField(self, field_name: typing_extensions___Literal[u"active_device_names",b"active_device_names",u"devices",b"devices"]) -> None: ...

class Device(google___protobuf___message___Message):
    DESCRIPTOR: google___protobuf___descriptor___Descriptor = ...
    name = ... # type: typing___Text
    device_properties = ... # type: typing___Text
    ignored_crash_signatures = ... # type: google___protobuf___internal___containers___RepeatedScalarFieldContainer[typing___Text]

    @property
    def preprocess(self) -> DevicePreprocess: ...

    @property
    def swift_shader(self) -> DeviceSwiftShader: ...

    @property
    def host(self) -> DeviceHost: ...

    @property
    def android(self) -> DeviceAndroid: ...

    @property
    def shader_compiler(self) -> DeviceShaderCompiler: ...

    @property
    def binaries(self) -> google___protobuf___internal___containers___RepeatedCompositeFieldContainer[gfauto___common_pb2___Binary]: ...

    def __init__(self,
        *,
        name : typing___Optional[typing___Text] = None,
        preprocess : typing___Optional[DevicePreprocess] = None,
        swift_shader : typing___Optional[DeviceSwiftShader] = None,
        host : typing___Optional[DeviceHost] = None,
        android : typing___Optional[DeviceAndroid] = None,
        shader_compiler : typing___Optional[DeviceShaderCompiler] = None,
        binaries : typing___Optional[typing___Iterable[gfauto___common_pb2___Binary]] = None,
        device_properties : typing___Optional[typing___Text] = None,
        ignored_crash_signatures : typing___Optional[typing___Iterable[typing___Text]] = None,
        ) -> None: ...
    @classmethod
    def FromString(cls, s: builtin___bytes) -> Device: ...
    def MergeFrom(self, other_msg: google___protobuf___message___Message) -> None: ...
    def CopyFrom(self, other_msg: google___protobuf___message___Message) -> None: ...
    if sys.version_info >= (3,):
        def HasField(self, field_name: typing_extensions___Literal[u"android",u"device",u"host",u"preprocess",u"shader_compiler",u"swift_shader"]) -> builtin___bool: ...
        def ClearField(self, field_name: typing_extensions___Literal[u"android",u"binaries",u"device",u"device_properties",u"host",u"ignored_crash_signatures",u"name",u"preprocess",u"shader_compiler",u"swift_shader"]) -> None: ...
    else:
        def HasField(self, field_name: typing_extensions___Literal[u"android",b"android",u"device",b"device",u"host",b"host",u"preprocess",b"preprocess",u"shader_compiler",b"shader_compiler",u"swift_shader",b"swift_shader"]) -> builtin___bool: ...
        def ClearField(self, field_name: typing_extensions___Literal[u"android",b"android",u"binaries",b"binaries",u"device",b"device",u"device_properties",b"device_properties",u"host",b"host",u"ignored_crash_signatures",b"ignored_crash_signatures",u"name",b"name",u"preprocess",b"preprocess",u"shader_compiler",b"shader_compiler",u"swift_shader",b"swift_shader"]) -> None: ...
    def WhichOneof(self, oneof_group: typing_extensions___Literal[u"device",b"device"]) -> typing_extensions___Literal["preprocess","swift_shader","host","android","shader_compiler"]: ...

class DeviceSwiftShader(google___protobuf___message___Message):
    DESCRIPTOR: google___protobuf___descriptor___Descriptor = ...

    def __init__(self,
        ) -> None: ...
    @classmethod
    def FromString(cls, s: builtin___bytes) -> DeviceSwiftShader: ...
    def MergeFrom(self, other_msg: google___protobuf___message___Message) -> None: ...
    def CopyFrom(self, other_msg: google___protobuf___message___Message) -> None: ...

class DevicePreprocess(google___protobuf___message___Message):
    DESCRIPTOR: google___protobuf___descriptor___Descriptor = ...

    def __init__(self,
        ) -> None: ...
    @classmethod
    def FromString(cls, s: builtin___bytes) -> DevicePreprocess: ...
    def MergeFrom(self, other_msg: google___protobuf___message___Message) -> None: ...
    def CopyFrom(self, other_msg: google___protobuf___message___Message) -> None: ...

class DeviceHost(google___protobuf___message___Message):
    DESCRIPTOR: google___protobuf___descriptor___Descriptor = ...

    def __init__(self,
        ) -> None: ...
    @classmethod
    def FromString(cls, s: builtin___bytes) -> DeviceHost: ...
    def MergeFrom(self, other_msg: google___protobuf___message___Message) -> None: ...
    def CopyFrom(self, other_msg: google___protobuf___message___Message) -> None: ...

class DeviceAndroid(google___protobuf___message___Message):
    DESCRIPTOR: google___protobuf___descriptor___Descriptor = ...
    serial = ... # type: typing___Text
    model = ... # type: typing___Text
    build_fingerprint = ... # type: typing___Text

    def __init__(self,
        *,
        serial : typing___Optional[typing___Text] = None,
        model : typing___Optional[typing___Text] = None,
        build_fingerprint : typing___Optional[typing___Text] = None,
        ) -> None: ...
    @classmethod
    def FromString(cls, s: builtin___bytes) -> DeviceAndroid: ...
    def MergeFrom(self, other_msg: google___protobuf___message___Message) -> None: ...
    def CopyFrom(self, other_msg: google___protobuf___message___Message) -> None: ...
    if sys.version_info >= (3,):
        def ClearField(self, field_name: typing_extensions___Literal[u"build_fingerprint",u"model",u"serial"]) -> None: ...
    else:
        def ClearField(self, field_name: typing_extensions___Literal[u"build_fingerprint",b"build_fingerprint",u"model",b"model",u"serial",b"serial"]) -> None: ...

class DeviceShaderCompiler(google___protobuf___message___Message):
    DESCRIPTOR: google___protobuf___descriptor___Descriptor = ...
    binary = ... # type: typing___Text
    args = ... # type: google___protobuf___internal___containers___RepeatedScalarFieldContainer[typing___Text]

    def __init__(self,
        *,
        binary : typing___Optional[typing___Text] = None,
        args : typing___Optional[typing___Iterable[typing___Text]] = None,
        ) -> None: ...
    @classmethod
    def FromString(cls, s: builtin___bytes) -> DeviceShaderCompiler: ...
    def MergeFrom(self, other_msg: google___protobuf___message___Message) -> None: ...
    def CopyFrom(self, other_msg: google___protobuf___message___Message) -> None: ...
    if sys.version_info >= (3,):
        def ClearField(self, field_name: typing_extensions___Literal[u"args",u"binary"]) -> None: ...
    else:
        def ClearField(self, field_name: typing_extensions___Literal[u"args",b"args",u"binary",b"binary"]) -> None: ...
