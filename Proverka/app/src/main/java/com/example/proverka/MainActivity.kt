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
import androidx.core.content.FileProvider
import com.example.proverka.Adapters.FoodUnRedactableAdapter
import com.example.proverka.databinding.ActivityMainBinding
import com.example.proverka.databinding.PrintFoodBinding
import com.example.proverka.model.FoodItem
import com.example.proverka.model.FoodList
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
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
        binding.fotobutton.setOnClickListener {onFotoPressed()}
        binding.editbutton.setOnClickListener {onEditPressed()}

    }

    private fun setupList() {
        unredadapter = FoodUnRedactableAdapter(data)
        binding.FoodListView.adapter = unredadapter
    }

    private fun onFotoPressed() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 100)
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
        if (requestCode == EDIT_REQUES_CODE  && resultCode == Activity.RESULT_OK) {
            data?.getParcelableArrayListExtra<FoodItem>(EditActivity.FOOD_LIST)?.let { this.data.setFood(it) }
        }
        else if (requestCode == 100) {//ответ с камеры

            val imageBitmap = data?.extras?.get("data") as Bitmap

//            val result1 = WeakReference(Bitmap.createScaledBitmap(imageBitmap,imageBitmap.height, imageBitmap.width,false).copy(Bitmap.Config.RGB_565,true))
//            val bm = result1.get() as Bitmap
//            val imageUri = saveImage(bm,this)

            println(saveImage(imageBitmap))

            val loggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)
            val gson = Gson()

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()


        }
        unredadapter.notifyDataSetChanged()
    }

    private fun saveImage(image: Bitmap): String? {
        var savedImagePath: String? = null
        val imageFileName = "JPEG_" + "FILE_NAME" + ".jpg"
        println(Environment.DIRECTORY_PICTURES.toString())
        val storageDir = File(Environment.DIRECTORY_PICTURES.toString())
        var seccess = true
        if (!storageDir.exists()) {
            seccess = storageDir.mkdir()
        }
        if (seccess) {
            val imageFile = File(storageDir, imageFileName)
            println(imageFile.absolutePath)
            savedImagePath = imageFile.absolutePath
            try {
                val fOut: OutputStream = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
                return savedImagePath
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return savedImagePath
    }

//    private fun saveImage(image: Bitmap, context: Context): Uri? {
//        val imagesFolder = File(context.cacheDir, "images")
//        var uri: Uri
//        try {
//            imagesFolder.mkdir()
//            val file = File(imagesFolder, "temp_image.jpg")
//            val stream = FileOutputStream(file)
//            image.compress(Bitmap.CompressFormat.JPEG,100,stream)
//            stream.flush()
//            stream.close()
//            uri = FileProvider.getUriForFile(context.applicationContext, "com.example.proverka"+".provider",file)
//            return uri
//        } catch (e : FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        return null
//    }

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

    private fun setupReadableList(){




    }

    companion object{
        @JvmStatic val FOOD_LIST = "FOOD_LIST"
        @JvmStatic val FOOD_TITLE = "FOOD_TITLE"
        @JvmStatic val EDIT_REQUES_CODE = 1
    }
}