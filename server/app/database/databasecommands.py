import typing
from app.mylogger import my_logger
from app.database.database_loaders import DataLoaderFromDatabase, DataUploadingToDatabase, QueryType


class NotFoundUserIdAndProductId(Exception):
    pass


class DatabaseCommands:
    async def try_add_user(self, user_token: str) -> bool:
        """
            Пытаемся добавить пользователя
        :param user_token:
        :return:
        """
        user_id = await self.__get_user_id(user_token)

        if user_id is not None:
            my_logger.info(f"Пользователь ({user_token}) уже есть в БД", "DatabaseCommands.TryAddUser")
            return False

        loader = DataUploadingToDatabase()
        await loader.upload_data_async(f"""insert into users(token) values ("{user_token}"); """)
        return True

    async def try_add_product(self, user_token: str, product_name: str, product_quantity: int) -> (bool, typing.Optional[list]):
        """
            Пытаемся добавить продукт
        :param product_quantity:
        :param user_token:
        :param product_name:
        :return:
        """
        user_id = await self.__get_user_id(user_token)
        product_id = await self.__get_product_id(user_id, product_name)

        # Если не нашли пользователя
        if user_id is None:
            my_logger.info(f"Пользователя ({user_token}) нет в БД", "DatabaseCommands.TryAddProduct")
            return False, None

        loader = DataUploadingToDatabase()
        if product_id is not None:
            loader_quantity = DataLoaderFromDatabase(QueryType.return_one)
            quantity = await loader_quantity.get_data_async(f"""select product_quantity from shopping_list where user_id = {user_id} and id = {product_id};""")
            if quantity is not None:
                await loader.upload_data_async(f"""update shopping_list set product_quantity = {quantity[0] + product_quantity} where user_id = {user_id} and id = {product_id};""")
        else:
            await loader.upload_data_async(f"""insert into shopping_list(user_id, product) values ({user_id}, "{product_name}"); """)
            product_id = await self.__get_product_id(user_id, product_name)
        return True, product_id

    async def try_change_product_name(self, user_token: str, product_id: int, product_name: str) -> bool:
        """
            Пытаемся изменить имя продукта по id продукту
        :param user_token:
        :param product_id:
        :param product_name:
        :return:
        """
        user_id = await self.__get_user_id(user_token)
        if user_id is None:
            my_logger.info(f"Пользователь ({user_token}) нет в БД", "DatabaseCommands.TryChangeProductName")
            return False

        loader = DataUploadingToDatabase()
        await loader.upload_data_async(f"""update shopping_list set product = "{product_name}" where user_id = {user_id} and id = {product_id};""")
        return True

    async def try_change_product_quantity(self, user_token: str, product_id: int, product_quantity: int) -> bool:
        """
            Пытаемся изменить кол-во продукта
        :param user_token:
        :param product_id:
        :param product_quantity:
        :return:
        """
        user_id = await self.__get_user_id(user_token)
        if user_id is None:
            my_logger.info(f"Пользователь ({user_token}) нет в БД", "DatabaseCommands.TryChangeProductQuantity")
            return False

        loader = DataUploadingToDatabase()
        await loader.upload_data_async(f"""update shopping_list set product_quantity = {product_quantity} where user_id = {user_id} and id = {product_id};""")
        return True

    async def try_delete_product(self, user_token: str, product_id: int, product_quantity: int) -> (bool, typing.Optional[list]):
        """
            Пытаемся удалить выбранный продукт из базы данных
        :param product_quantity:
        :param product_id:
        :param user_token:
        :return:
        """
        user_id = await self.__get_user_id(user_token)

        if user_id is None:
            my_logger.info(f"Пользователь ({user_token}) нет в БД", "DatabaseCommands.TryDeleteProduct")
            return False, None

        product_name_from_db = await self.__get_product_name(user_id, product_id)
        if product_name_from_db is None:
            my_logger.info(f"Продукта ({product_id}) нет в БД", "DatabaseCommands.TryDeleteProduct")
            return False, None

        loader_quantity = DataLoaderFromDatabase(QueryType.return_one)
        quantity = await loader_quantity.get_data_async(f"""select product_quantity from shopping_list where user_id = {user_id} and id = {product_id};""")

        loader = DataUploadingToDatabase()
        # Если у нас больше одного продукта
        if quantity is not None and quantity[0] - product_quantity > 0:
            quantity = quantity[0]
            await loader.upload_data_async(f"""update shopping_list set product_quantity = {quantity - product_quantity} where user_id = {user_id} and id = {product_id};""")
        else:
            await loader.upload_data_async(f"""delete from shopping_list where user_id = {user_id} and id = {product_id};""")
        return True, product_name_from_db

    async def try_delete_all_products(self, user_token: str) -> bool:
        """
            Пытаемся удалить все продукты пользователя
        :param user_token:
        :return:
        """
        user_id = await self.__get_user_id(user_token)

        if user_id is None:
            my_logger.info(f"Пользователь ({user_token}) нет в БД", "DatabaseCommands.TryDeleteProduct")
            return False

        loader = DataUploadingToDatabase()
        await loader.upload_data_async(f"""delete from shopping_list where user_id = {user_id};""")
        return True

    async def try_get_all_products_user(self, user_token: str, add_ids: bool = False) -> (bool, typing.Optional[list]):
        """
            Пытаемся получить все продукты выбранного пользователя
        :param user_token:
        :param add_ids:
        :return:
        """
        user_id = await self.__get_user_id(user_token)

        if user_id is None:
            my_logger.info(f"Пользователя ({user_token}) нет в БД", "DatabaseCommands.TryGetAllProductsUser")
            return False, None
        return await self.__try_get_data_from_shopping_list(user_id, add_ids)

    async def try_get_all_selected_products_user(self, user_token: str, add_ids: bool = False) -> (bool, typing.Optional[list]):
        """
            Пытаемся получить все выбранные продукты выбранного продукта
        :param user_token:
        :param add_ids:
        :return:
        """
        user_id = await self.__get_user_id(user_token)

        if user_id is None:
            my_logger.info(f"Пользователя ({user_token}) нет в БД", "DatabaseCommands.TryGetAllSelectedProductsUser")
            return False, None

        return await self.__try_get_data_from_shopping_list(user_id, add_ids, True)

    async def try_get_all_not_selected_products_user(self, user_token: str, add_ids: bool = False) -> (bool, typing.Optional[list]):
        user_id = await self.__get_user_id(user_token)

        if user_id is None:
            my_logger.info(f"Пользователя ({user_token}) нет в БД", "DatabaseCommands.TryGetAllNotSelectedProductsUser")
            return False, None

        return await self.__try_get_data_from_shopping_list(user_id, add_ids, False)

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
            my_logger.info(f"Пользователя ({user_token}) нет в БД", "DatabaseCommands.TryClearUserProducts")
            return False

        loader = DataUploadingToDatabase()
        await loader.upload_data_async(f"""delete from shopping_list where user_id = {user_id};""")
        return True

    async def __try_get_user_and_product_ids(self, user_token: str, product_name: str) -> (int, int):
        user_id = await self.__get_user_id(user_token)
        product_id = await self.__get_product_id(user_id, product_name)

        if user_id is None or product_id is None:
            my_logger.warning(f"Пользователя ({user_token}) нет в БД или продукт ({product_name}) нет в БД", "DatabaseCommands.TryGetUserAndProductsIds")
            raise NotFoundUserIdAndProductId()

        return user_id, product_id

    @staticmethod
    async def try_get_all_tokens() -> (bool, typing.Optional[list]):
        loader = DataLoaderFromDatabase(QueryType.return_all)
        tokens = await loader.get_data_async(f"""select token from users""")
        if tokens is None or len(tokens) == 0:
            return False, None
        return True, tokens

    @staticmethod
    async def __try_get_data_from_shopping_list(user_id: int, add_ids: bool = False, is_purchased_product: typing.Optional[bool] = None) -> (bool, typing.Optional[list]):
        loader = DataLoaderFromDatabase(QueryType.return_all)

        current_id = current_is_purchased_value = ""
        if add_ids:
            current_id = "id,"
        if is_purchased_product is not None:
            current_is_purchased_value = f"and is_purchased_product = {is_purchased_product}"

        command = f"""select {current_id} product from shopping_list where user_id = {user_id} {current_is_purchased_value};"""
        products = await loader.get_data_async(command)
        return True, products

    @staticmethod
    async def __get_product_id(user_id: int, product_name: str) -> typing.Optional[int]:
        loader = DataLoaderFromDatabase(QueryType.return_one)
        data = await loader.get_data_async(f"""select id from shopping_list where user_id = {user_id} and product = "{product_name}";""")
        return None if data is None else data[0]

    @staticmethod
    async def __get_product_name(user_id: int, product_id: int) -> typing.Optional[str]:
        loader = DataLoaderFromDatabase(QueryType.return_one)
        data = await loader.get_data_async(f"""select id from shopping_list where user_id = {user_id} and id = {product_id};""")
        return None if data is None else data[0]

    @staticmethod
    async def __get_user_id(user_token: str) -> typing.Optional[int]:
        loader = DataLoaderFromDatabase(QueryType.return_one)
        data = await loader.get_data_async(f"""select id from users where token = "{user_token}";""")
        return None if data is None else data[0]


database_commands = DatabaseCommands()
