import asyncio
import sqlite3
import typing
from abc import ABC, abstractmethod
from enum import Enum
from sqlite3 import Connection

from app.mylogger import my_logger
from app.core.config import get_config
from app.core.pathutils import get_database_root

config = get_config()


class QueryType(Enum):
    return_one = 0
    return_all = 1
    commit = 2


class QueryAnswer(Enum):
    failed = 0
    success = 1


class DatabaseQuery(ABC):
    def __init__(self, action_on_success: typing.Callable) -> None:
        self._command: typing.Optional[str] = None
        self._action_on_success = action_on_success

    @property
    @abstractmethod
    def type(self) -> QueryType:
        pass

    def set_command(self, command: str) -> str:
        self._command = command

    def get_command(self) -> str:
        if self._command is None:
            raise Exception()  # todo filter error
        return self._command

    def success(self, result: typing.Any) -> None:
        self._action_on_success(result)


class DatabaseQueryCommit(DatabaseQuery):

    @property
    def type(self) -> QueryType:
        return QueryType.commit


class DatabaseQueryReturnOne(DatabaseQuery):

    @property
    def type(self) -> QueryType:
        return QueryType.return_one


class DatabaseQueryReturnMany(DatabaseQuery):

    @property
    def type(self) -> QueryType:
        return QueryType.return_all


class DatabaseHandler:
    def __init__(self) -> None:
        self._path = get_database_root()
        self._connector: typing.Optional[Connection] = None
        self._queue: typing.List[DatabaseQuery] = []
        self._is_running = False

    def add_request_to_database(self, query: DatabaseQuery) -> None:
        self._queue.append(query)

    @asyncio.coroutine
    async def run_process_requests(self) -> None:
        if self._is_running:
            return
        self._is_running = True

        while True:
            await asyncio.sleep(config.code.delay_async)
            self.__process_requests()

    def __process_requests(self) -> None:
        if len(self._queue) == 0:
            return
        self.__connect()
        while len(self._queue) != 0:
            first_query = self._queue.pop(0)
            command = first_query.get_command()
            cursor = self._connector.execute(command)

            if first_query.type == QueryType.return_one:
                result = cursor.fetchone()
                first_query.success(result)
                my_logger.info("Запрос с получением одного значения завершен")
            elif first_query.type == QueryType.return_all:
                result = cursor.fetchall()
                first_query.success(result)
                my_logger.info("Запрос с получением множества значений завершен")
            elif first_query.type == QueryType.commit:
                self._connector.commit()
                first_query.success(QueryAnswer.success)
                my_logger.info("Запрос с сохранением результата завершен")
            else:
                my_logger.warning("Не верный тип запроса")
        self.__disconnect()

    def __connect(self) -> None:
        if self._connector is not None:
            my_logger.warning("Подключение к базе данных уже установлено")
            return
        self._connector = sqlite3.connect(self._path)

    def __disconnect(self) -> None:
        if self._connector is None:
            my_logger.warning("База данных еще не отключена")
            return
        self._connector.close()
        self._connector = None


database_handler = DatabaseHandler()
