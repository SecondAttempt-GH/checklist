package com.example.proverka

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.proverka.Adapters.FoodUnRedactableAdapter
import com.example.proverka.databinding.ActivityMainBinding
import com.example.proverka.databinding.PrintFoodBinding
import com.example.proverka.model.AuthResp.AuthoResponseBody
import com.example.proverka.model.FoodItem
import com.example.proverka.model.FoodList
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var data: FoodList
    private lateinit var token: String
    private lateinit var unredadapter: FoodUnRedactableAdapter
    val contentType = "application/json".toMediaType()
    val serverUrl = ServerUrl("https://5d59-176-77-61-151.eu.ngrok.io")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState ==null || !savedInstanceState.containsKey(FOOD_LIST)){
            data = FoodList()
        } else {
            savedInstanceState.getParcelableArrayList<FoodItem>(FOOD_LIST)?.let { data.setFood(it) }
        }

        setupToken()
        setupList()
        binding.addButton.setOnClickListener {onAddPressed()}
        binding.fotobutton.setOnClickListener {onFotoPressed()}//кнопка с камерой
        binding.editbutton.setOnClickListener {onEditPressed()}

    }

    private fun setupToken(){
        val sPref = getPreferences(MODE_PRIVATE)
        val editor = sPref.edit()
        var savedToken = sPref.getString(SAVED_TOKEN, "")
        token = savedToken.toString()
        println(token)
        Toast.makeText(this, savedToken, Toast.LENGTH_SHORT).show();
        if (savedToken == "") {
            val gson = Gson()
            val client = OkHttpClient.Builder()
                .build()
            val request = Request.Builder()
                .post(gson.toJson(null).toRequestBody(contentType))// если здесь сделать get запрос
                .url(serverUrl.authorization_user)// а здесь поставить адрес ютуба то метод call.enqueue снизу работает
                .build()

            val call = client.newCall(request)



            call.enqueue(object : Callback {//здесь погибли мои надежды
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBodyString = response.body!!.string()
                        val jsonResp = gson.fromJson(responseBodyString, AuthoResponseBody::class.java)
                        editor.putString(SAVED_TOKEN, jsonResp.message.values.token)
                        token = jsonResp.message.values.token
                        editor.commit()


                    } else {
                        throw IllegalStateException("Oops")
                    }


                }
            })

        }


    }

    private fun setupList() {
        unredadapter = FoodUnRedactableAdapter(data)
        binding.FoodListView.adapter = unredadapter
    }

    private fun onFotoPressed() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)// ACTION_IMAGE_CAPTURE это системная активити которая возвращает фотку в формате BMP
        startActivityForResult(intent, 100)// отлов приходящего результата происходит в методе onActivityResult по коду 100
    }

    private fun onAddPressed() {
        val dialogBinding: PrintFoodBinding = PrintFoodBinding.inflate(layoutInflater)
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle("Create")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { d, which ->
                val name = dialogBinding.editTextTextFoodName.text.toString()
                val num = dialogBinding.editTextTextFoodNum.text.toString()
                if (name.isNotBlank()) {
                    if (num.isNotBlank()) createFood(name,num)
                    else createFood(name)
                }
            }.create()
        dialog.show()
    }

    private fun onEditPressed() {
        val intent = Intent(this, EditActivity::class.java)
        intent.putParcelableArrayListExtra(FOOD_LIST, data.getFoods())
        startActivityForResult(intent, EDIT_REQUES_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_REQUES_CODE  && resultCode == RESULT_OK) {
            data?.getParcelableArrayListExtra<FoodItem>(EditActivity.FOOD_LIST)?.let { this.data.setFood(it) }// это перенос изменений в основном списке, если в EditAction чето вернула (возвращает сипок если его изменили в ручную)
        }
        else if (requestCode == 100) {//ответ с камеры
//////////Перевод ответа в Jpg//////////////////////////
            val imageBitmap = data?.extras?.get("data") as Bitmap


            val savedJPGImage = saveImage(imageBitmap, this) as File

//////////////Пример работы с Http запросами/////////////////////


            val stream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()



            val loggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)
            val gson = Gson()
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

//            val requestBodyString = gson.toJson(savedJPGImage.readBytes())


//            val okHttpRequestBody = requestBodyString.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val okHttpRequestBody = byteArray.toRequestBody("multipart/form-data".toMediaType(),0, byteArray.size)
            val body = MultipartBody.Part.createFormData("photo[content]", "photo", byteArray.toRequestBody("image/*".toMediaTypeOrNull(),0, byteArray.size))
            println(byteArray.toRequestBody("image/*".toMediaTypeOrNull(),0, byteArray.size).contentType())
            val body2 = MultipartBody.Builder()
                .setType("multipart/form-data".toMediaType())
                .addFormDataPart("user_token", token)
                .addFormDataPart("file", "photo_bytes", byteArray.toRequestBody("bytes".toMediaTypeOrNull(),0, byteArray.size))
                .build()


            val request = Request.Builder()
                .post(body2)
                .addHeader("accept", "application/json")
                .url(serverUrl.check_photo_with_list_of_products + "?user_token=" + token)
//                .url("https://webhook.site/85b64cb6-4a29-4ce3-9d67-647e7b2cec77" + "?user_token=" + token)
                .build()
            val call = client.newCall(request)
            var grespons : Response
//////// крч если я не успею дописать логику за ночь то запросы прописываются следующим образом/////////////
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBodyString = response.body!!.toString()
                        println(gson.fromJson(responseBodyString, String::class.java))
                    }

                }
            })
//////////////////////////////////////////////////////////////////////////////////////////////////////////


        }
        unredadapter.notifyDataSetChanged()
    }

    private fun saveImage(image: Bitmap, context: Context): File? {
        val imageFolder = File(context.cacheDir, "images")
        try {
            imageFolder.mkdir()
            val file = File(imageFolder, "tmp_picture.jpg")
            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            return file
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    private fun createFood(name: String) {
        data.add(name)
        unredadapter.notifyDataSetChanged()
    }

    private fun createFood(name: String, num: String) {
        data.add(name, num)
        unredadapter.notifyDataSetChanged()
    }

    private fun decripPosition(name: String) {
        data.decFood(name)
    }



    /////////это вообще забей хуй (Коды ответов кри навигации между активити, чтобы не хардкодить вынес их сюда)///////////////
    companion object{
        @JvmStatic val SAVED_TOKEN = "saved_token"
        @JvmStatic val FOOD_LIST = "FOOD_LIST"
        @JvmStatic val FOOD_TITLE = "FOOD_TITLE"
        @JvmStatic val EDIT_REQUES_CODE = 1
    }
}