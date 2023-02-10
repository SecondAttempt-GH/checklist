import typing

from app.my_requests.answerbuilder import AnswerBuilder, AnswerStatus


def get_products_answer(user_token: str, state: bool, products: typing.Optional[list]) -> str:
    answer = AnswerBuilder()
    if state:
        answer.set_status(AnswerStatus.success).set_comment(
            f"Получены продукты пользователя ({user_token})")
        result = []
        for product in products:
            result.append({"product_id": product[0], "product_name": product[1]})
        return answer.add_value("product_list", result).get_result()
    answer.set_status(AnswerStatus.error). \
        set_comment(f"Не удалось получить список продуктов пользователя ({user_token})")
    return answer.add_value("product_list", None).get_result()