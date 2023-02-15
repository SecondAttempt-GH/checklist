package com.example.proverka.model


data class Message<T>(
    val comment: String?,
    val values: T?
)


abstract class BaseResponse<T> {
    abstract val message: Message<T>?
    abstract val status: String
}


data class AddProductResponse(
    override val message: Message<AddProductValues>?,
    override val status: String
) : BaseResponse<AddProductValues>()


data class AuthenticationResponse(
    override val message: Message<AuthenticationValues>?,
    override val status: String
) : BaseResponse<AuthenticationValues>()


data class DeleteProductResponse(
    override val message: Message<DeleteProductValues>?,
    override val status: String
) : BaseResponse<DeleteProductValues>()


data class GetAllProductsResponse(
    override val message: Message<AllProductValues>?,
    override val status: String
) : BaseResponse<AllProductValues>()

data class CheckPhotoResponse(
    override val message: Message<PhotoValues>?,
    override val status: String
) : BaseResponse<PhotoValues>()


data class EditProductResponse(
    override val message: Message<EditProductValues>?,
    override val status: String
) : BaseResponse<EditProductValues>()