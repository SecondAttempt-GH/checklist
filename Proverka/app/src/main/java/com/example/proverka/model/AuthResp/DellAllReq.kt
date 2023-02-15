package com.example.proverka.model.AuthResp

data class DellAllReq (
    val message: DellAllReqMessage,
    val status: String
        )

data class DellAllReqMessage (
    val comment: String
)
