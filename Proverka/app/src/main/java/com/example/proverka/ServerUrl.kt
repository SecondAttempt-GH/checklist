package com.example.proverka

class ServerUrl(
    val baseUrl : String
)
{
    val check_photo_with_list_of_products = baseUrl+"/check_photo_with_list_of_products"
    val authorization_user = baseUrl+"/authorization_user"
    val add_product = baseUrl+"/add_product"
    val delete_produt = baseUrl+"/delete_produt"
    val edit_product = baseUrl+"/edit_product"

}