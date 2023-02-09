import typing

from app.database.database_loaders import DataLoaderFromDatabase, DataUploadingToDatabase, QueryType


class NotFoundUserIdAndProductId(Exception):
    pass


class DatabaseCommands:
    def try_add_user(self, user_token: str) -> bool:
        user_id = await self.__get_user_id(user_token)

        if user_id is None:
            return False

        loader = DataUploadingToDatabase()
        await loader.upload_data_async(f"""insert into users(token) values ("{user_token}"); """)
        return True

    async def try_add_product(self, user_token: str, product_name: str) -> bool:
        user_id = await self.__get_user_id(user_token)
        product_id = await self.__get_product_id(user_id, product_name)

        # Если не нашли пользователя или нашли продукт с таким именим
        if user_id is None or product_id is not None:
            return False

        loader = DataUploadingToDatabase()
        await loader.upload_data_async(f"""insert into shopping_list(user_id, product) values ({user_id}, "{product_name}"); """)
        return True

    async def try_change_name_product(self, user_token: str, old_product_name: str, new_product_name: str) -> bool:
        try:
            user_id, product_id = await self.__try_get_user_and_product_ids(user_token, old_product_name)

            loader = DataUploadingToDatabase()
            await loader.upload_data_async(f"""update shopping_list set product = "{new_product_name}" where user_id = {user_id} and id = {product_id};""")
            return True
        except NotFoundUserIdAndProductId:
            return False

    async def try_delete_product(self, user_token: str, product_name: str) -> bool:
        """
            Пытаемся удалить выбранный продукт из базы данных
        :param user_token:
        :param product_name:
        :return:
        """
        try:
            user_id, product_id = await self.__try_get_user_and_product_ids(user_token, product_name)

            loader = DataUploadingToDatabase()
            await loader.upload_data_async(f"""delete from shopping_list where user_id = {user_id} and id = {product_id};""")
            return True
        except NotFoundUserIdAndProductId:
            return False

    async def try_select_product(self, user_token: str, product_name: str) -> bool:
        """
            Пытаемся указать, что продукт был найден на фото и мы его вычеркиваем
        :param user_token:
        :param product_name:
        :return:
        """
        try:
            user_id, product_id = await self.__try_get_user_and_product_ids(user_token, product_name)

            loader = DataUploadingToDatabase()
            await loader.upload_data_async(f"""update shopping_list set is_purchased_product = True where user_id = {user_id} and id = {product_id};""")
            return True
        except NotFoundUserIdAndProductId:
            return False

    async def try_clear_user_products(self, user_token: str) -> bool:
        """
            Пытаемся удалить все продукты пользователя
        :param user_token:
        :return:
        """
        user_id = await self.__get_user_id(user_token)

        # Если пользователя нет в БД
        if user_id is None:
            return False

        loader = DataUploadingToDatabase()
        await loader.upload_data_async(f"""delete from shopping_list where user_id = {user_id};""")
        return True

    async def __try_get_user_and_product_ids(self, user_token: str, product_name: str) -> (int, int):
        user_id = await self.__get_user_id(user_token)
        product_id = await self.__get_product_id(user_id, product_name)

        if user_id is None or product_id is None:
            raise NotFoundUserIdAndProductId()

        return user_id, product_id

    @staticmethod
    async def __get_product_id(user_id, product_name: str) -> typing.Optional[str]:
        loader = DataLoaderFromDatabase(QueryType.return_one)
        data = await loader.get_data_async(f"""select id from shopping_list where user_id = {user_id} and product = "{product_name}";""")
        return data

    @staticmethod
    async def __get_user_id(user_token: str) -> typing.Optional[str]:
        loader = DataLoaderFromDatabase(QueryType.return_one)
        data = await loader.get_data_async(f"""select id from users where token = "{user_token}";""")
        return data
