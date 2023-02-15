package com.example.proverka.model

import com.google.gson.annotations.SerializedName


data class AddProductValues(
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("productName")
    val productName: String
)

data class AuthenticationValues(
    val token: String
)

data class DeleteProductValues(
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("product_name")
    val productName: String
)

data class AllProductValues(
    @SerializedName("product_list")
    val productList: Array<ProductValue>?
)

data class ProductValue(
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("product_quantity")
    val productQuantity: Int
)

data class PhotoValues(
    @SerializedName("found_text")
    val foundText: String,
    @SerializedName("product")
    val productName: String,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("time_spent")
    val timeSpent: Double
)

data class EditProductValues(
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("new_product_name")
    val newProductName: String?,
    @SerializedName("new_product_quantity")
    val newProductQuantity: Int?
)