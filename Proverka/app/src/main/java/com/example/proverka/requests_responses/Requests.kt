package com.example.proverka.model

import com.google.gson.annotations.SerializedName

data class AddProductRequest(
    @SerializedName("user_token")
    val userToken: String,
    @SerializedName("product_name")
    val productName: String?,
    @SerializedName("product_quantity")
    val productQuantity: Int?
)


data class DeleteProductRequest(
    @SerializedName("user_token")
    val userToken: String,
    @SerializedName("product_id")
    val productId: Int
)

data class EditProductRequest(
    @SerializedName("user_token")
    val userToken: String,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("product_name")
    val productName: String?,
    @SerializedName("product_quantity")
    val productQuantity: Int?
)

data class TokenRequest(
    @SerializedName("user_token")
    val userToken: String
)