package com.example.proverka

class ServerUrl(
    baseUrl: String
) {
    val checkPhotoWithListOfProducts = "$baseUrl/check_photo_with_list_of_products"
    val authorizationUser = "$baseUrl/authorization_user"
    val addProduct = "$baseUrl/add_product"
    val deleteProduct = "$baseUrl/delete_product"
    val editProduct = "$baseUrl/edit_product"
    val getAllProducts = "$baseUrl/get_all_products"
    val deleteAllProducts = "$baseUrl/delete_all_products"

}