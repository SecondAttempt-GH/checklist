import asyncio
import typing
from app.core.config import get_config
from app.database.databasehander import DatabaseQueryReturnMany, DatabaseQueryReturnOne, DatabaseQueryCommit
from app.database.databasehander import database_handler, QueryType, QueryAnswer


config = get_config()


class DataLoaderFromDatabase:
    """
        Загрузчик данных из БД
    """

    def __init__(self, type_query: QueryType) -> None:
        if type_query is QueryType.commit:
            raise Exception()  # todo filter error

        self._data: typing.Any = None
        self._is_loading = False

        if type_query is QueryType.return_one:
            self._query = DatabaseQueryReturnOne(self.__update_one_data)
        else:
            self._query = DatabaseQueryReturnMany(self.__update_many_data)

    async def get_data_async(self, command: str) -> typing.Any:
        if self._is_loading:
            raise Exception()  # todo filter error
        self._query.set_command(command)
        database_handler.add_request_to_database(self._query)
        await self.__loading_data()
        return self._data

    async def __loading_data(self) -> None:
        self._data = None
        self._is_loading = True
        while self._is_loading:
            await asyncio.sleep(config.code.delay_async)
            if not self._is_loading:
                return

    def __update_one_data(self, result) -> None:
        self._data = None if result is None else result
        self._is_loading = False

    def __update_many_data(self, result) -> None:
        self._data = list() if result is None else [answer for answer in result]
        self._is_loading = False


class DataUploadingToDatabase:
    """
        Загрузчик данных в БД
    """

    def __init__(self) -> None:
        self._result: typing.Optional[QueryAnswer] = None
        self._is_loading = False

        self._query = DatabaseQueryCommit(self.__successful_download)

    async def upload_data_async(self, command: str) -> None:
        if self._is_loading:
            raise Exception()  # todo filter error
        self._query.set_command(command)
        database_handler.add_request_to_database(self._query)
        await self.__loading_result()

    async def __loading_result(self) -> None:
        self._result = None
        self._is_loading = True
        while self._is_loading:
            await asyncio.sleep(config.code.delay_async)
            if not self._is_loading:
                return

    def __successful_download(self, value) -> None:
        self._result = value
        self._is_loading = False
