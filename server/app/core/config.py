import json
import typing

from pydantic import BaseModel, validator

from app.core.pathutils import get_config_root


class Code(BaseModel):
    delay_async: float
    overwrite_logs: bool
    write_in_file_logs: bool


class ML(BaseModel):
    conf_threshold: float
    nms_threshold: float
    languages: typing.List[str]

    @validator("languages")
    def check_languages(cls, value) -> typing.List[str]:
        if value is None or len(value) == 0:
            return ["ru"]
        return value


class PCA(BaseModel):
    min_len_words: int
    sub_token_len: int
    threshold_word: float
    threshold_sentence: float


class Config(BaseModel):
    code: Code
    ml: ML
    pca: PCA


__factory: typing.Optional[Config] = None


def get_config() -> Config:
    global __factory

    if __factory is None:
        config_root = get_config_root()
        with open(config_root, 'r', encoding="utf-8") as fr:
            data = json.loads(fr.read())
            __factory = Config(**data)
    return __factory
