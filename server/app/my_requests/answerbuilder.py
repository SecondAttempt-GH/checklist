import typing
from enum import Enum


class AnswerStatus(str, Enum):
    error = "error"
    success = "success"


class AnswerBuilder:
    def __init__(self) -> None:
        self._status: typing.Optional[AnswerStatus] = None
        self._comment: typing.Optional[str] = None
        self._values: typing.Dict = {}

    def set_status(self, status: AnswerStatus):
        self._status = status
        return self

    def set_comment(self, comment: str):
        self._comment = comment
        return self

    def add_value(self, key: str, value: typing.Any):
        self._values[key] = value
        return self

    def get_result(self) -> dict:
        result = {"message": {}}
        if self._status is not None:
            result["status"] = self._status.value
        if self._comment is not None:
            result["message"]["comment"] = self._comment
        if len(self._values.keys()) != 0:
            result["message"]["values"] = self._values
        return result
