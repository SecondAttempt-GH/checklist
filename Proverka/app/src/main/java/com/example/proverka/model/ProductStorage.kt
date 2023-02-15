package com.example.proverka.model

import android.util.Log

class ProductStorage {

    private var products: ArrayList<ProductItem> = arrayListOf()

    fun getProducts(): ArrayList<ProductItem> {
        return products
    }

    fun updateProducts(updatedProducts: ArrayList<ProductItem>) {
        products.clear()
        products = updatedProducts
    }

    fun getProduct(index: Int): ProductItem? {
        if(index < 0 || index >= products.size){
            return null
        }
        return products[index]
    }

    fun getSize(): Int {
        return products.size
    }

    fun addProduct(product: ProductAnswer) {
        val isExistsProduct = products.indexOfFirst {it.productId == product.productId}
        if(isExistsProduct == product.productId){
            Log.d("ProductStorage", "Product with id: ${product.productId} is existing in database")
            return
        }
        var correctedQuantity = 1
        if(product.productQuantity != null){
            correctedQuantity = product.productQuantity
        }
        val item = ProductItem(
            product.productId,
            product.productName,
            correctedQuantity
        )
        products.add(item)
    }

    fun editNameProduct(id: Int, newNameProduct: String) {
        val index: Int = products.indexOfFirst { it.productId == id }
        if (index == -1) {
            Log.d("ProductStorage", "Product with id: $id is not existing")
            return
        }
        val product = products[index]
        product.change()
        product.productName = newNameProduct
    }

    fun remove(id: Int) {
        val index: Int = products.indexOfFirst { it.productId == id }
        if (index == -1) {
            Log.d("ProductStorage", "Product with id: $id is not existing")
            return
        }
        val product = products[index]
        product.remove()
    }

    fun addOnceProduct(id: Int) {
        val index: Int = products.indexOfFirst { it.productId == id }
        if (index == -1) {
            Log.d("ProductStorage", "Product with id: $id is not existing")
            return
        }
        val product = products[index]
        product.change()
        product.productQuantity++
    }

    fun removeOnceProduct(id: Int) {
        val index: Int = products.indexOfFirst { it.productId == id }
        if (index == -1) {
            Log.d("ProductStorage", "Product with id: $id is not existing")
            return
        }
        val product = products[index]
        if(product.productQuantity - 1 > 1){
            product.change()
            product.productQuantity--
        }
    }
}