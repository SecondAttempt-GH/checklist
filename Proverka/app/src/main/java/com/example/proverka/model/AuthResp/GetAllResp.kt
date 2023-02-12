package com.example.proverka.model.AuthResp

data class GetAllResp (
    val message : GetAllRespMessage,
    val status: String
)


data class ValuesReq (
    val product_list: Array<Product>?
)

data class Product (
    val product_id: Int,
    val product_name: String,
    val product_quantity: Int
    )

data class GetAllRespMessage (
    val comment: String,
    val values: ValuesReq?
)
