from fastapi import File, FastAPI, Depends

from app.algorithmforcomparingoffers import AlgorithmForComparingOffers
from app.core.authorizationutils import generate_token
from app.database.databasecommands import database_commands
from app.determinanttextofpricetag import DeterminantTextOfPriceTag
from app.my_requests.answerbuilder import AnswerBuilder, AnswerStatus
from app.my_requests.commands_utils import get_products_answer
from app.schemas import AddProductSchema
from app.schemas import DeleteProductSchema
from app.schemas import EditProductsSchema
from app.schemas import GetAllProductsSchema
from app.schemas import GetSelectedProductsSchema
from app.schemas import GetNotSelectedProductsSchema
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
    answer = AnswerBuilder()
    condition, products = await database_commands.try_get_all_products_user(request.user_token)
    if not condition:
        answer.set_status(AnswerStatus.error).set_comment("Не удалось получить данные из БД")
        return answer.get_result()

    determinant = DeterminantTextOfPriceTag()
    result_determine = determinant.to_determine(photo_bytes)

    if result_determine.found_text is None:
        answer.set_status(AnswerStatus.error). \
            set_comment("Не удалось найти текст на изображение"). \
            add_value("image", "None")
        return answer.get_result()

    algorithm = AlgorithmForComparingOffers()
    for product in products:
        coincidence = algorithm.check(product, result_determine.found_text)
        if coincidence:
            answer.set_status(AnswerStatus.success). \
                set_comment(f"Продукт {product} вычеркнут из списка"). \
                add_value("product", product). \
                add_value("found_text", result_determine.found_text). \
                add_value("time_spent", result_determine.time_spent)
            return answer.get_result()
    answer.set_status(AnswerStatus.error). \
        set_comment(
        f"Не удалось найти продукт ({result_determine.found_text}) в списках пользователя ({request.user_token})")
    return answer.get_result()


@app.post("/authorization_user")
async def authorization_user():
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
    created_token = generate_token()
    condition, all_tokens = await database_commands.try_get_all_tokens()
    while condition and created_token in all_tokens:
        created_token = generate_token()

    is_add_new_user = await database_commands.try_add_user(created_token)
    answer = AnswerBuilder()
    if is_add_new_user:
        answer.set_status(AnswerStatus.success). \
            set_comment("Новый пользователь добавлен в БД"). \
            add_value("token", created_token)
        return answer.get_result()
    return answer.set_status(AnswerStatus.error).set_comment("Не удалось добавить пользователя в БД").get_result()


@app.post("/get_all_products")
async def get_all_products(request: GetAllProductsSchema):
    """
        Получение всех продуктов, которые есть у пользователя
    :param request:
    :return:
    """
    state, products = await database_commands.try_get_all_products_user(request.user_token, True)
    return get_products_answer(request.user_token, state, products)


@app.post("/get_selected_products")
async def get_selected_products(request: GetSelectedProductsSchema):
    """
        Получение всех выбранных продуктов пользователя
    :param request:
    :return:
    """

    state, products = await database_commands.try_get_all_selected_products_user(request.user_token, True)
    return get_products_answer(request.user_token, state, products)


@app.post("/get_all_not_selected_products_user")
async def get_all_not_selected_products_user(request: GetNotSelectedProductsSchema):
    """
        Получение всех не выбранных продуктов пользователя
    :param request:
    :return:
    """
    state, products = await database_commands.try_get_all_not_selected_products_user(request.user_token, True)
    return get_products_answer(request.user_token, state, products)


@app.post("/edit_product")
async def edit_product(request: EditProductsSchema):
    """
        Редактирование продукта
        Можно изменить имя продукта, а так же установить что продукт уже был выбран
    :param request:
    :return:
    """
    user_token = request.user_token
    product_id = request.product_id
    product_name = request.product_name

    answer = AnswerBuilder()
    state = await database_commands.try_change_product_name(user_token, product_id, product_name)
    if state:
        answer.set_status(AnswerStatus.success).\
            set_comment(f"Удалось изменить имя продукта для пользователя ({user_token})").\
            add_value("product_id", product_id).add_value("new_product_name", product_name)
        return answer.get_result()
    answer.set_status(AnswerStatus.error).set_comment(f"Не удалось изменить имя продукта для пользователя ({user_token})")
    return answer.get_result()


@app.post("/delete_product")
async def delete_product(request: DeleteProductSchema):
    """
        Удаление продукта из бд
    :param request:
    :return:
    """
    user_token = request.user_token
    product_id = request.product_id

    answer = AnswerBuilder()
    state, product_name = await database_commands.try_delete_product(user_token, product_id)
    if state:
        answer.set_status(AnswerStatus.success).\
            set_comment(f"Удалось удалить продукт для пользователя ({user_token})").\
            add_value("product_id", product_id).add_value("product_name", product_name)
        return answer.get_result()
    answer.set_status(AnswerStatus.error).set_comment(f"Не удалось удалить имя продукта для пользователя ({request.user_token})")
    return answer.get_result()


@app.post("/add_product")
async def add_product(request: AddProductSchema):
    """
        Добавление нового продукта
    :param request:
    :return:
    """
    user_token = request.user_token
    product_name = request.product_name

    answer = AnswerBuilder()
    state, product_id = await database_commands.try_add_product(user_token, product_name)
    if state:
        answer.set_status(AnswerStatus.success).\
            set_comment(f"Удалось добавить продукт для пользователя ({user_token})").\
            add_value(product_id, product_id).add_value("product_name", product_name)
        return answer.get_result()
    answer.set_status(AnswerStatus.error).set_comment(f"Не удалось добавить продукт для пользователя ({request.user_token})")
    return answer.get_result()
