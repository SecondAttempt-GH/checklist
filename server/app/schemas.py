import typing

from fastapi import File
from pydantic import BaseModel


class RequestWithMandatoryUserId(BaseModel):
    user_id: str


class AuthorizationUserSchema(BaseModel):
    user_id: typing.Optional[str]
    add_list_of_products: typing.Optional[bool]


class PhotoUserSchema(RequestWithMandatoryUserId):
    pass


class GetAllProductsSchema(RequestWithMandatoryUserId):
    pass


class GetSelectedProductsSchema(RequestWithMandatoryUserId):
    pass


class EditProductsSchema(RequestWithMandatoryUserId):
    product_id: int
    name_changed: bool
    new_name: typing.Optional[str]
    product_selected: bool


class DeleteProductSchema(RequestWithMandatoryUserId):
    product_id: int


class AddProductSchema(RequestWithMandatoryUserId):
    name: str
