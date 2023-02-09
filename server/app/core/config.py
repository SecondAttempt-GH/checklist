import json
import typing

from pydantic import BaseModel

from app.core.pathutils import get_config_root


class Code(BaseModel):
    delay_async: float
    overwrite_logs: bool
    write_in_file_logs: bool


class Config(BaseModel):
    code: Code


__factory: typing.Optional[Config] = None


def get_config() -> Config:
    global __factory

    if __factory is None:
        config_root = get_config_root()
        with open(config_root, 'r', encoding="utf-8") as fr:
            data = json.loads(fr.read())
            __factory = Config(**data)
    return __factory
