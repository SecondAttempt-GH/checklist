package ru.samsung.case2022

import android.app.Application
import com.example.proverka.model.ProductStorage

class App : Application(){
    val productStorage =  ProductStorage()
}