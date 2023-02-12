package com.example.proverka.model

data class FotoRequest (
    val message : FotoRequestMessage,
    val status: String
    )

class ValuesReq (
    val product: String,
    val found_text: String,
    val time_spend: Long
    )

class FotoRequestMessage (
    val comment: String,
    val value: ValuesReq?
    )


