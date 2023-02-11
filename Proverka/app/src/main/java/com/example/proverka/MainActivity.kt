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
import androidx.core.content.FileProvider
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.proverka.Adapters.FoodUnRedactableAdapter
import com.example.proverka.databinding.ActivityMainBinding
import com.example.proverka.databinding.PrintFoodBinding
import com.example.proverka.model.FoodItem
import com.example.proverka.model.FoodList
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.WeakReference



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var data: FoodList
    private lateinit var unredadapter: FoodUnRedactableAdapter
    val contentType = "application/json; charset=utf-8".toMediaType()
    val serverUrl = "https://webhook.site/85b64cb6-4a29-4ce3-9d67-647e7b2cec77"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState ==null || !savedInstanceState.containsKey(FOOD_LIST)){
            data = FoodList()
        } else {
            savedInstanceState.getParcelableArrayList<FoodItem>(FOOD_LIST)?.let { data.setFood(it) }
        }

        setupList()
        binding.addButton.setOnClickListener {onAddPressed()}
        binding.fotobutton.setOnClickListener {onFotoPressed()}//кнопка с камерой
        binding.editbutton.setOnClickListener {onEditPressed()}

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

//////////////Попытака работы с Http запросами в синхронном формате (неудачная. В андроид приложениях работа с http запросами выводится в отдельный поток(ассинхронно с использованием короутинов))/////////////////////


            val loggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)
            val gson = Gson()


            val client = OkHttpClient.Builder()
                .build()

            val requestBodyString = gson.toJson(savedJPGImage.readBytes())

            println(requestBodyString)
            val okHttpRequestBody = requestBodyString.toRequestBody(contentType)
            val request = Request.Builder()
                .post(okHttpRequestBody)
                .url(serverUrl)
                .build()
            val call = client.newCall(request)

            val response = call.execute()

            if (response.isSuccessful) {
                println("+")
            }



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
        @JvmStatic val FOOD_LIST = "FOOD_LIST"
        @JvmStatic val FOOD_TITLE = "FOOD_TITLE"
        @JvmStatic val EDIT_REQUES_CODE = 1
    }
}