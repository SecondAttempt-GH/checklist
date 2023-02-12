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
    product_quantity: typing.Optional[int]


class DeleteProductSchema(RequestWithMandatoryUserToken):
    product_id: int
    product_quantity: int = 1


class AddProductSchema(RequestWithMandatoryUserToken):
    product_name: str
    product_quantity: int = 1


class DeleteAllSchema(RequestWithMandatoryUserToken):
    pass
