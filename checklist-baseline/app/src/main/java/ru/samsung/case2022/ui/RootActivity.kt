package ru.samsung.case2022.ui

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proverka.Adapters.FoodUnRedactableAdapter
import com.example.proverka.handlers.LoaderDataFromServer
import com.example.proverka.handlers.QueueOfProductsForUpdating
import com.example.proverka.handlers.TokenHandler
import com.example.proverka.model.ProductAnswer
import com.example.proverka.model.ProductItem
import com.example.proverka.model.ProductStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.samsung.case2022.App
import ru.samsung.case2022.R
import ru.samsung.case2022.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream
import java.io.IOException


const val PRODUCT = "PRODUCT"
const val PRODUCT_NAME_EDIT = "PRODUCT_NAME_EDIT"
const val PRODUCT_QUANTITY_EDIT = "PRODUCT_QUANTITY_EDIT"
const val PRODUCT_ID = "PRODUCT_REMOVED_ID"
const val TOKEN = "TOKEN"
const val IMAGE_FROM_CAMERA = "IMAGE_FROM_CAMERA"
const val EDIT_REQUEST_CODE = 1
const val REQUEST_CAMERA = 2
const val PREVIEW_REQUEST_CODE = 3
const val FOUND_PRODUCT_NAME = "FOUND_PRODUCT_NAME"
const val FOUND_PRODUCT_ID = "FOUND_PRODUCT_ID"
const val EDIT_ON_MAIN_REQUEST_CODE = 4
const val ADD_REQUEST_CODE = 5


class RootActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var unredadapter: FoodUnRedactableAdapter
    private var currentImageUri: Uri? = null

    private val productStorage: ProductStorage
        get() = (applicationContext as App).productStorage
    private var tokenHandler: TokenHandler = TokenHandler(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutManager = LinearLayoutManager(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initApp()
        supportActionBar?.title = "Чек-Лист"
        binding.add.setOnClickListener { onAddPressed() }
        binding.scan.setOnClickListener { onPhotoPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        run {
            if (isGranted) {
                Log.i("Permission: ", "Granted")
            } else {
                Log.i("Permission: ", "Denied")
            }
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                requestPermissionLauncher.launch(
                    android.Manifest.permission.CAMERA
                )
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.CAMERA
            ) -> {
                requestPermissionLauncher.launch(
                    android.Manifest.permission.CAMERA
                )
            }

            else -> {
                requestPermissionLauncher.launch(
                    android.Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun initApp() {
//        checkCameraPermission()

        GlobalScope.launch {
            val token = tokenHandler.getToken() ?: return@launch

            val loaderProducts = LoaderDataFromServer()
            val products = loaderProducts.getData(token) ?: return@launch
            for (product in products) {
                productStorage.addProduct(product)
            }

            runOnUiThread {
                unredadapter = FoodUnRedactableAdapter(this@RootActivity) { onPressed(it) }
                unredadapter.products = productStorage
                binding.recycler.layoutManager = layoutManager
                binding.recycler.adapter = unredadapter
            }
        }
    }

    private fun onPressed(v: View) {
        val product = v.tag as ProductItem
        val intent = Intent(this, EditActivityOnMain::class.java)
        val bundle = Bundle()
        bundle.putParcelable(PRODUCT, product)
        intent.putExtra("bundle", bundle)
        startActivityForResult(intent, EDIT_ON_MAIN_REQUEST_CODE)
    }

    private fun onPhotoPressed() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "NewPhoto")
        values.put(MediaStore.Images.Media.DESCRIPTION, "FromCamera")

        currentImageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri)
        startActivityForResult(intent, REQUEST_CAMERA)
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
                unredadapter = FoodUnRedactableAdapter(this@RootActivity) { onPressed(it) }
                unredadapter.products = productStorage
                binding.recycler.layoutManager = layoutManager
                binding.recycler.adapter = unredadapter
            }
        }
    }

    private fun onAddPressed() {
        val intent = Intent(this, AddActivity::class.java)
        startActivityForResult(intent, ADD_REQUEST_CODE)
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
                if (changedProducts.isNotEmpty()) {
                    runOnUiThread {
                        data?.getParcelableArrayListExtra<ProductItem>(EditActivity.FOOD_LIST)
                            ?.let { productStorage.updateProducts(it) }
                        unredadapter.notifyDataSetChanged()
                    }
                }
            }
        } else if (requestCode == EDIT_ON_MAIN_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val bundle = data.getBundleExtra("bundle") ?: return
            val productName = bundle.getString(PRODUCT_NAME_EDIT)
            val productQuantity = bundle.getInt(PRODUCT_QUANTITY_EDIT)
            val productID = bundle.getInt(PRODUCT_ID)

            val selectedProduct = productStorage.getProductById(productID) ?: return
            selectedProduct.productName = productName
            selectedProduct.productQuantity = productQuantity

            unredadapter.notifyDataSetChanged()

            GlobalScope.launch {
                val token = tokenHandler.getToken() ?: return@launch
                val loader = LoaderDataFromServer()
                loader.editProduct(
                    token, ProductAnswer(
                        productID, productName, productQuantity
                    )
                )
            }
        } else if (requestCode == EDIT_ON_MAIN_REQUEST_CODE && resultCode == RESULT_CANCELED && data != null) {
            val bundle = data.getBundleExtra("bundle") ?: return
            val productID = bundle.getInt(PRODUCT_ID)
            productStorage.remove(productID)
            unredadapter.notifyDataSetChanged()

            GlobalScope.launch {
                val token = tokenHandler.getToken() ?: return@launch
                val loader = LoaderDataFromServer()
                loader.deleteProduct(
                    token, ProductAnswer(
                        productID, "productName", 1
                    )
                )
            }
        } else if (requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK) {
            val name = data?.extras?.get("AddElementName")
            val num = data?.extras?.get("AddElementNum")
            addProductToServer(name as String, num as Int)
        }

        //ответ с камеры
        else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            val intent = Intent(this, CameraActivity::class.java)
            try {
                val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImageUri)
                val byteArrayImage = getByteArray(imageBitmap)

                GlobalScope.launch {
                    val bundle = Bundle()
                    bundle.putString(TOKEN, tokenHandler.getToken())
                    bundle.putByteArray(IMAGE_FROM_CAMERA, byteArrayImage)

                    intent.putExtra("bundle", bundle)
                    startActivityForResult(intent, PREVIEW_REQUEST_CODE)
                }
            } catch (e: java.lang.Exception) {
                return
            }
        } else if (requestCode == PREVIEW_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val bundle = data.getBundleExtra("bundle")
            if (bundle == null) {
                showInfo("Продукт не найден")
                return
            }

            val productName = bundle.getString(FOUND_PRODUCT_NAME)
            val productId = bundle.getInt(FOUND_PRODUCT_ID)

            if (productName == null || productId == -1) {
                showInfo("Продукт не найден")
            } else {
                showInfo("Продукт ${productName} вычеркнут")
                productStorage.remove(productId)
                val correctedProducts =
                    productStorage.getProducts().filter { p -> !p.isRemoved() }
                productStorage.updateProducts(correctedProducts as ArrayList<ProductItem>)
                unredadapter.notifyDataSetChanged()
            }
        }
    }

    private fun getByteArray(
        bitmap: Bitmap,
    ): ByteArray {
        var stream = ByteArrayOutputStream()
        val byteArray: ByteArray

        try {
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)) {
                throw IOException("Failed to save bitmap.");
            }
            byteArray = stream.toByteArray()
        } finally {
            stream.close()
        }

        return byteArray
    }

    private fun showInfo(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}