package com.example.proverka.handlers

import com.example.proverka.model.ProductAnswer
import kotlinx.coroutines.delay


// Тут не самый лучший код, много дубликата, но по другому не придумал ((
class LoaderDataFromServer {
    private var isLoading: Boolean = false
    private lateinit var loadedProducts: Array<ProductAnswer>
    private var loadedToken: String? = null
    private var loadedProductAnswer: ProductAnswer? = null

    suspend fun getData(userToken: String): Array<ProductAnswer>? {
        if (isLoading) {
            return null
        }

        RequestHandler.sendRequestForGetAllProducts(userToken) { result ->
            loadedProducts = result
            isLoading = false
        }

        isLoading = true
        while (isLoading) {
            delay(100)
        }
        return loadedProducts
    }

    suspend fun getData(): String? {
        if (isLoading) {
            return null
        }

        RequestHandler.sendRequestForGetToken { result ->
            loadedToken = result
            isLoading = false
        }

        isLoading = true
        while (isLoading) {
            delay(100)
        }
        return loadedToken
    }

    suspend fun editProduct(userToken: String, updatedProduct: ProductAnswer): ProductAnswer? {
        if (isLoading) {
            return null
        }

        RequestHandler.sendRequestForEditProduct(userToken, updatedProduct) { result ->
            loadedProductAnswer = result
            isLoading = false
        }

        isLoading = true
        while (isLoading) {
            delay(100)
        }
        return loadedProductAnswer
    }

    suspend fun addProduct(userToken: String, addedProduct: ProductAnswer): ProductAnswer? {
        if (isLoading) {
            return null
        }

        RequestHandler.sendRequestForAddProduct(userToken, addedProduct) {result ->
            loadedProductAnswer = result
            isLoading = false
        }

        isLoading = true
        while (isLoading) {
            delay(100)
        }
        return loadedProductAnswer
    }

    suspend fun checkPhoto(userToken: String, photoBytes: ByteArray): ProductAnswer? {
        if (isLoading) {
            return null
        }

        RequestHandler.sendRequestForCheckPhoto(userToken, photoBytes) {result ->
            loadedProductAnswer = result
            isLoading = false
        }

        isLoading = true
        while (isLoading) {
            delay(100)
        }
        return loadedProductAnswer
    }

    suspend fun deleteProduct(userToken: String, deleteProduct: ProductAnswer) {
        if (isLoading) {
            return
        }

        RequestHandler.sendRequestForDeleteProduct(userToken, deleteProduct) {
            isLoading = false
        }

        isLoading = true
        while (isLoading) {
            delay(100)
        }
    }
}