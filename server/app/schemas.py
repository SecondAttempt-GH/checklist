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
    old_name: str
    new_name: typing.Optional[str]


class DeleteProductSchema(RequestWithMandatoryUserToken):
    product_name: str


class AddProductSchema(RequestWithMandatoryUserToken):
    product_name: str
