package com.example.proverka.model.AuthResp

data class AddResp (
        val message : AddRespMessage,
        val status: String
)

data class AddProduct (
        val product_id: Int,
        val product_name: String
)

data class AddRespMessage (
        val comment: String,
        val value: AddProduct?
)