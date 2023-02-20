package com.example.proverka.handlers

import androidx.appcompat.app.AppCompatActivity

const val DEFAULT_TOKEN_VALUE = "None"
const val SAVED_TOKEN = "SAVED_TOKEN"

class TokenHandler(private var app: AppCompatActivity) {
    suspend fun getToken() : String? {
        val preferences = app.getPreferences(AppCompatActivity.MODE_PRIVATE)
        val savedToken = preferences.getString(SAVED_TOKEN, DEFAULT_TOKEN_VALUE)

        if(savedToken == null || savedToken == DEFAULT_TOKEN_VALUE){
            val loader = LoaderDataFromServer()
            val token = loader.getData() ?: return null
            val editor = preferences.edit()
            editor.putString(SAVED_TOKEN, token)
            editor.apply()
            return token
        }
        return savedToken
    }
}