package com.example.proverka.model

data class FotoRequest (
    val message : FotoRequestMessage,
    val status: String
    )

class ValuesReq (
    val image: String
    )

class FotoRequestMessage (
    val comment: String,
    val value: ValuesReq
    )


