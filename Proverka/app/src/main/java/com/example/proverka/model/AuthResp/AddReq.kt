package com.example.proverka.model.AuthResp

data class AddReq (
    val user_token: String,
    val product_name: String,
    val product_quantity: Int
        )