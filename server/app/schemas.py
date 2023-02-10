import typing

from pydantic import BaseModel


class RequestWithMandatoryUserToken(BaseModel):
    user_token: str


class PhotoUserSchema(RequestWithMandatoryUserToken):
    pass


class GetAllProductsSchema(RequestWithMandatoryUserToken):
    pass


class GetSelectedProductsSchema(RequestWithMandatoryUserToken):
    pass


class GetNotSelectedProductsSchema(RequestWithMandatoryUserToken):
    pass


class EditProductsSchema(RequestWithMandatoryUserToken):
    product_id: int
    product_name: typing.Optional[str]


class DeleteProductSchema(RequestWithMandatoryUserToken):
    product_id: int


class AddProductSchema(RequestWithMandatoryUserToken):
    product_name: str
