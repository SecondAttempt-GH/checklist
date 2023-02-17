package com.example.proverka.handlers

import android.util.Log
import com.example.proverka.ServerUrl
import com.example.proverka.model.*
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import java.io.IOException


val applicationContentType = "application/json".toMediaType()
val multipartFormData = "multipart/form-data".toMediaType()
val serverUrl = ServerUrl("http://5.188.141.141")


object RequestHandler {
    private var TAG = "RequestHandler"

    fun sendRequestForGetAllProducts(
        userToken: String,
        onSuccess: (result: Array<ProductAnswer>) -> Unit
    ) {
        val call = getCallRequest(serverUrl.getAllProducts, TokenRequest(userToken))

        sendRequest(call, GetAllProductsResponse::class.java) { response ->
            val products = response.message?.values?.productList
            val productsArray = arrayListOf<ProductAnswer>()

            if (products == null) {
                onSuccess(productsArray.toTypedArray())
                return@sendRequest
            }

            for (product in products) {
                productsArray.add(
                    ProductAnswer(
                        product.productId, product.productName, product.productQuantity
                    )
                )
            }
            onSuccess(productsArray.toTypedArray())
        }
    }

    fun sendRequestForGetToken(onSuccess: (result: String?) -> Unit) {
        val call = getCallRequest(serverUrl.authorizationUser, null)

        sendRequest(call, AuthenticationResponse::class.java) { response ->
            val token = response.message?.values?.token

            if (token == null) {
                onSuccess(null)
                return@sendRequest
            }
            onSuccess(token)
        }
    }

    fun sendRequestForEditProduct(
        userToken: String,
        updatedProduct: ProductAnswer,
        onSuccess: (product: ProductAnswer) -> Unit
    ) {
        val call = getCallRequest(
            serverUrl.editProduct,
            EditProductRequest(
                userToken,
                updatedProduct.productId,
                updatedProduct.productName,
                updatedProduct.productQuantity
            )
        )

        sendRequest(call, EditProductResponse::class.java) { response ->
            val product = response.message?.values ?: return@sendRequest

            onSuccess(
                ProductAnswer(
                    product.productId,
                    product.newProductName,
                    product.newProductQuantity
                )
            )
        }
    }

    fun sendRequestForDeleteProduct(
        userToken: String,
        deleteProduct: ProductAnswer,
        onSuccess: () -> Unit
    ) {
        val call = getCallRequest(
            serverUrl.deleteProduct,
            DeleteProductRequest(userToken, deleteProduct.productId)
        )

        sendRequest(call, DeleteProductResponse::class.java) { response ->
            response.message?.values ?: return@sendRequest
            onSuccess()
        }
    }

    fun sendRequestForAddProduct(
        userToken: String,
        addedProduct: ProductAnswer,
        onSuccess: (product: ProductAnswer) -> Unit
    ) {
        if (addedProduct.productName == null) {
            return
        }

        val call = getCallRequest(
            serverUrl.addProduct,
            AddProductRequest(userToken, addedProduct.productName, addedProduct.productQuantity)
        )

        sendRequest(call, AddProductResponse::class.java) { response ->
            val newProduct = response.message?.values ?: return@sendRequest
            onSuccess(
                ProductAnswer(
                    newProduct.productId,
                    addedProduct.productName,
                    addedProduct.productQuantity
                )
            )
        }
    }

    fun sendRequestForCheckPhoto(
        userToken: String,
        photoBytes: ByteArray,
        onSuccess: (product: ProductAnswer) -> Unit
    ) {
        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        val gson = Gson()
        val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val body = MultipartBody.Builder()
            .setType(multipartFormData)
            .addFormDataPart("user_token", userToken)
            .addFormDataPart(
                "file",
                "photo_bytes",
                photoBytes.toRequestBody("bytes".toMediaTypeOrNull(), 0, photoBytes.size)
            )
            .build()

        val request = Request.Builder()
            .post(body)
            .addHeader("accept", "application/json")
            .url(serverUrl.checkPhotoWithListOfProducts)
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful && response.body != null) {
                        val responseString = response.body!!.string()
                        val jsonResp = gson.fromJson(responseString, CheckPhotoResponse::class.java)
                        if (getCheckStatusAndMessageResponse(jsonResp.status, jsonResp.message)) {
                            val values = jsonResp.message?.values!!
                            onSuccess(ProductAnswer(values.productId, values.productName, 1))
                        }
                    }
                } catch (e: IOException) {
                    e.localizedMessage?.let { Log.e(TAG, it) };
                } catch (e: JSONException) {
                    e.localizedMessage?.let { Log.e(TAG, it) };
                }
            }
        })
    }

    private fun <TClass, TMessage> sendRequest(
        call: Call,
        classResponse: Class<TClass>,
        onSuccess: (response: BaseResponse<TMessage>) -> Unit
    ) where TClass : BaseResponse<TMessage> {
        val gson = Gson()
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful && response.body != null) {
                        val responseString = response.body!!.string()
                        val jsonResp =
                            gson.fromJson(responseString, classResponse) as BaseResponse<TMessage>
                        if (getCheckStatusAndMessageResponse(jsonResp.status, jsonResp.message)) {
                            onSuccess(jsonResp)
                        }
                    }
                }
                catch (e: IOException) {
                    e.localizedMessage?.let { Log.e(TAG, it) };
                } catch (e: JSONException) {
                    e.localizedMessage?.let { Log.e(TAG, it) };
                }
            }
        })
    }

    private fun <T> getCheckStatusAndMessageResponse(
        status: String,
        message: Message<T>?
    ): Boolean {
        return status.lowercase() == "success" && message != null && message.values != null
    }

    private fun getCallRequest(url: String, jsonElement: Any?): Call {
        val request = getRequest(url, jsonElement)
        val client = getClient()

        return client.newCall(request)
    }

    private fun getRequest(url: String, jsonElement: Any?): Request {
        return Request.Builder()
            .post(
                Gson().toJson(jsonElement).toRequestBody(applicationContentType)
            ).url(url).build()
    }

    private fun getClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }
}