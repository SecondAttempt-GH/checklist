from fastapi import File, FastAPI, Depends

from app.determinanttextofpricetag import DeterminantTextOfPriceTag
from app.schemas import AddProductSchema
from app.schemas import AuthorizationUserSchema
from app.schemas import DeleteProductSchema
from app.schemas import EditProductsSchema
from app.schemas import GetAllProductsSchema
from app.schemas import GetSelectedProductsSchema
from app.schemas import PhotoUserSchema

from app.database.databasecommands import database_commands

app = FastAPI()


@app.post("/check_photo_with_list_of_products")
async def check_photo_with_list_of_products(request: PhotoUserSchema = Depends(), photo_bytes: bytes = File(...)):
    """
        Получает фотку и проверяет есть ли продукт в списке с таким же именим
    :param request:
    :param photo_bytes:
    :return:
    """
    determinant = DeterminantTextOfPriceTag()
    result = determinant.to_determine(photo_bytes)
    if result.found_text is None:
        return {"status": "error", "message": "Не удалось определить ценник и найти на нем текст"}

    return {
        "status": "success",
        "found_text": result.found_text
        # "format": image_file.format,
        # "user_id": request.user_token
    }


@app.post("/authorization_user")
async def authorization_user(request: AuthorizationUserSchema):
    """
        Авторизация пользователя. Если не передан user_id, регистрируем пользователя
        Так же с можно получить все продукты, которые есть у пользователя
    :param request:
    :return:
    """

    # Логика следующая:
    # Сначала попробуем добавить пользователя
    # Если получится, он добавиться в БД, в противном случае ничего не добавиться)
    # Далее смотрим, нужен ли клиенту список продуктов, если нужен достаем из БД и возвращаем
    is_add_new_user = database_commands.try_add_user(request.user_token)

    if request.add_list_of_products is None or request.add_list_of_products is False:
        return {"status": "success", "message": {"is_new_user": is_add_new_user, "products": None}}

    state, products = await database_commands.try_get_all_products_user(request.user_token)
    if state:
        return {"status": "success", "message": {"is_new_user": is_add_new_user, "products": products}}
    return {"status": "error", "message": "Не удалось получить список продуктов"}


@app.post("/get_all_products")
async def get_all_products(request: GetAllProductsSchema):
    """
        Получение всех продуктов, которые есть у пользователя
    :param request:
    :return:
    """
    state, products = await database_commands.try_get_all_products_user(request.user_token)
    if state:
        return {"status": "success", "message": {"products": products}}
    return {"status": "error", "message": "Не удалось получить список продуктов"}


@app.post("/get_selected_products")
async def get_selected_products(request: GetSelectedProductsSchema):
    """
        Получение всех выбранных продуктов пользователя
    :param request:
    :return:
    """
    state, products = await database_commands.try_get_all_selected_products_user(request.user_token)
    if state:
        return {"status": "success", "message": {"products": products}}
    return {"status": "error", "message": "Не удалось получить список выбранных продуктов"}


@app.post("/edit_product")
async def edit_product(request: EditProductsSchema):
    """
        Редактирование продукта
        Можно изменить имя продукта, а так же установить что продукт уже был выбран
    :param request:
    :return:
    """
    user_token = request.user_token
    old_name = request.old_name
    new_name = request.new_name

    state = await database_commands.try_change_name_product(user_token, old_name, new_name)
    if state:
        return {"status": "success", "message": {"old_product_name": old_name, "new_product_name": new_name}}
    return {"status": "error", "message": "Не удалось изменить имя продукта"}


@app.post("/delete_product")
async def delete_product(request: DeleteProductSchema):
    """
        Удаление продукта из бд
    :param request:
    :return:
    """
    user_token = request.user_token
    product_name = request.product_name

    state = await database_commands.try_delete_product(user_token, product_name)
    if state:
        return {"status": "success", "message": {"deleted_product": product_name}}
    return {"status": "error", "message": "Не удалось удалить продукт"}


@app.post("/add_product")
async def add_product(request: AddProductSchema):
    """
        Добавление нового продукта
    :param request:
    :return:
    """
    user_token = request.user_token
    product_name = request.product_name

    state = await database_commands.try_add_product(user_token, product_name)
    if state:
        return {"status": "success", "message": {"added_product": product_name}}
    return {"status": "error", "message": "Не удалось добавить продукт"}
