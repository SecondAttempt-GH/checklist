import asyncio
import sqlite3
import typing
from abc import ABC, abstractmethod
from enum import Enum
from sqlite3 import Connection

from app.core.pathutils import get_database_root


class QueryType(Enum):
    return_one = 0
    return_many = 1
    commit = 2


class DatabaseQuery(ABC):
    def __init__(self, command: str, action_on_success: typing.Callable) -> None:
        self._command = command
        self._action_on_success = action_on_success

    @property
    @abstractmethod
    def type(self) -> QueryType:
        pass

    def get_command(self) -> str:
        return self._command

    def success(self, result: typing.Any) -> None:
        if self.type == QueryType.commit:
            return

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
        return QueryType.return_many


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
            await asyncio.sleep(0.5)
            self.__process_requests()

    def __process_requests(self) -> None:
        if len(self._queue) == 0:
            return
        self.__connect()
        while len(self._queue) != 0:
            first_query = self._queue[0]
            command = first_query.get_command()
            cursor = self._connector.execute(command)

            if first_query.type == QueryType.return_one:
                result = cursor.fetchone()
                first_query.success(result)
            elif first_query.type == QueryType.return_many:
                result = cursor.fetchmany()
                first_query.success(result)
            elif first_query.type == QueryType.commit:
                self._connector.commit()
            else:
                print("Error.DatabaseHandler.ProcessRequests")  # todo error
            self._queue.remove(first_query)
        self.__disconnect()

    def __connect(self) -> None:
        if self._connector is not None:
            print("Error.DatabaseHandler.Connect")  # todo log
            return
        self._connector = sqlite3.connect(self._path)

    def __disconnect(self) -> None:
        if self._connector is None:
            print("Error.DatabaseHandler.Disconnect")  # todo log
            return
        self._connector.close()
        self._connector = None


database_handler = DatabaseHandler()
