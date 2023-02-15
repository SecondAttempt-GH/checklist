package com.example.proverka

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import com.example.proverka.Adapters.FoodUnRedactableAdapter
import com.example.proverka.databinding.ActivityMainBinding
import com.example.proverka.databinding.PrintFoodBinding
import com.example.proverka.handlers.LoaderDataFromServer
import com.example.proverka.handlers.QueueOfProductsForUpdating
import com.example.proverka.handlers.TokenHandler
import com.example.proverka.model.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


private const val FOOD_LIST = "FOOD_LIST"
private const val EDIT_REQUEST_CODE = 1
private const val REQUEST_CAMERA = 2

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var productStorage: ProductStorage
    private lateinit var imageUri: Uri
    private lateinit var unredadapter: FoodUnRedactableAdapter
    private var tokenHandler: TokenHandler = TokenHandler(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initApp()
        supportActionBar?.title = "Чек-Лист"
        binding.addButton.setOnClickListener { onAddPressed() }
        binding.fotobutton.setOnClickListener { onPhotoPressed() }
        binding.editbutton.setOnClickListener { onEditPressed() }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    private fun initApp() {
        GlobalScope.launch {
            val token = tokenHandler.getToken() ?: return@launch

            val loaderProducts = LoaderDataFromServer()
            val products = loaderProducts.getData(token) ?: return@launch
            productStorage = ProductStorage()
            for (product in products) {
                productStorage.addProduct(product)
            }

            runOnUiThread {
                unredadapter = FoodUnRedactableAdapter(productStorage)
                binding.FoodListView.adapter = unredadapter
            }
        }
    }

    private fun onPhotoPressed() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        imageUri =
            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(
            intent,
            REQUEST_CAMERA
        )
    }

    private fun addProductToServer(productName: String, productQuantity: Int) {
        GlobalScope.launch {
            val token = tokenHandler.getToken() ?: return@launch

            val loaderProducts = LoaderDataFromServer()
            val addedProduct =
                loaderProducts.addProduct(token, ProductAnswer(-1, productName, productQuantity))
                    ?: return@launch
            productStorage.addProduct(addedProduct)

            runOnUiThread {
                unredadapter = FoodUnRedactableAdapter(productStorage)
                binding.FoodListView.adapter = unredadapter
            }
        }
    }

    private fun onAddPressed() {
        val dialogBinding: PrintFoodBinding = PrintFoodBinding.inflate(layoutInflater)
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle("Создать")
            .setView(dialogBinding.root)
            .setPositiveButton("Добавить") { d, which ->
                val name = dialogBinding.editTextTextFoodName.text.toString()
                val num = dialogBinding.editTextTextFoodNum.text.toString()
                if (name.isNotBlank()) {
                    if (num.isNotBlank()) {
                        addProductToServer(name, num.toInt())
                    } else {
                        addProductToServer(name, 1)
                    }
                }
            }.create()
        dialog.show()
    }

    private fun onEditPressed() {
        val intent = Intent(this, EditActivity::class.java)
        intent.putParcelableArrayListExtra(FOOD_LIST, productStorage.getProducts())
        startActivityForResult(intent, EDIT_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            GlobalScope.launch {
                val token = tokenHandler.getToken() ?: return@launch

                val changedProducts = QueueOfProductsForUpdating.getAndClearProducts()

                val loader = LoaderDataFromServer()
                for (product in changedProducts) {
                    val productForRequest = ProductAnswer(
                        product.productId,
                        product.productName,
                        product.productQuantity
                    )
                    // Порядок важен, так как можем сначала поменять кол-во, а потом удалить
                    if (product.isRemoved()) {
                        loader.deleteProduct(token, productForRequest)
                    } else if (product.isChanged()) {
                        loader.editProduct(token, productForRequest)
                    }
                }
                if(changedProducts.isNotEmpty()){
                    runOnUiThread {
                        data?.getParcelableArrayListExtra<ProductItem>(EditActivity.FOOD_LIST)?.let { productStorage.updateProducts(it) }
                        unredadapter.notifyDataSetChanged()
                    }
                }
            }
        }
        //ответ с камеры
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            GlobalScope.launch {
                val token = tokenHandler.getToken() ?: return@launch
                val imageBitmap: Bitmap

                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri)
                } catch (e: java.lang.Exception) {
                    return@launch
                }

                val stream = ByteArrayOutputStream()

                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val byteArray = stream.toByteArray()

                val loaderPhoto = LoaderDataFromServer()
                val foundProduct = loaderPhoto.checkPhoto(token, byteArray)

                if (foundProduct != null) {
                    runOnUiThread {
                        productStorage.remove(foundProduct.productId)
                        val correctedProducts = productStorage.getProducts().filter { p -> !p.isRemoved() }
                        productStorage.updateProducts(correctedProducts as ArrayList<ProductItem>)
                        unredadapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}