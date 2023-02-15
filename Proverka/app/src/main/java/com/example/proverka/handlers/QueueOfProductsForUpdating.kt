package com.example.proverka.handlers

import com.example.proverka.model.ProductItem

// Используется для хранения продуктов, которые были изменены
object QueueOfProductsForUpdating {
    private var products: ArrayList<ProductItem> = arrayListOf()

    fun addChangedProduct(product: ProductItem){
        if(!products.contains(product)) {
            products.add(product)
        }
    }

    fun getAndClearProducts(): Array<ProductItem> {
        val temp = products.toTypedArray()
        products.clear()
        return temp
    }
}