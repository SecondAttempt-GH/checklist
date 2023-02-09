import io

from PIL import Image
from fastapi import File, FastAPI, Depends

from app.schemas import AddProductSchema
from app.schemas import AuthorizationUserSchema
from app.schemas import DeleteProductSchema
from app.schemas import EditProductsSchema
from app.schemas import GetAllProductsSchema
from app.schemas import GetSelectedProductsSchema
from app.schemas import PhotoUserSchema

app = FastAPI()


@app.post("/check_photo_with_list_of_products")
async def check_photo_with_list_of_products(request: PhotoUserSchema = Depends(), photo_bytes: bytes = File(...)):
    """
        Получает фотку и проверяет есть ли продукт в списке с таким же именим
    :param request:
    :param photo_bytes:
    :return:
    """
    image_stream = io.BytesIO(photo_bytes)
    image_file = Image.open(image_stream)
    return {
        "status": "success",
        "size": image_file.size,
        "format": image_file.format,
        "user_id": request.user_id
    }


@app.post("/authorization_user")
async def authorization_user(request: AuthorizationUserSchema):
    """
        Авторизация пользователя. Если не передан user_id, регистрируем пользователя
        Так же с можно получить все продукты, которые есть у пользователя
    :param request:
    :return:
    """
    return {"status": "success",
            "message": {"user_id": "id пользователя", "list_products": "Тут будет список продуктов"}}


@app.post("/get_all_products")
async def get_all_products(request: GetAllProductsSchema):
    """
        Получение всех продуктов, которые есть у пользователя
    :param request:
    :return:
    """
    return {"status": "success", "message": {"all_products": "Тут будет список продуктов"}}


@app.post("/edit_product")
async def edit_product(request: EditProductsSchema):
    """
        Редактирование продукта
        Можно изменить имя продукта, а так же установить что продукт уже был выбран
    :param request:
    :return:
    """
    return {"status": "success", "message": {"product_id": "id продукта"}}


@app.post("/get_selected_products")
async def get_selected_products(request: GetSelectedProductsSchema):
    """
        Получение всех выбранных продуктов пользователя
    :param request:
    :return:
    """
    return {"status": "success", "message": {"all_selected_products": "тут будет список всех выбранных продуктов"}}


@app.post("/delete_product")
async def delete_product(request: DeleteProductSchema):
    """
        Удаление продукта из бд
    :param request:
    :return:
    """
    return {"status": "success", "message": {"delete_product": "id продукта"}}


@app.post("/add_product")
async def add_product(request: AddProductSchema):
    """
        Добавление нового продукта
    :param request:
    :return:
    """
    return {"status": "success", "message": {"new_product": "id продукта"}}
