package ru.samsung.case2022.ui

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.example.proverka.handlers.LoaderDataFromServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.samsung.case2022.R


private const val TAG = "CameraActivity"
class CameraActivity : AppCompatActivity() {


    private var dataToken: String? = null
    private lateinit var dataImage: ByteArray

    private lateinit var previous: ImageView
    private lateinit var cancelBtn: Button
    private lateinit var recognizeBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val bundle = intent.getBundleExtra("bundle")
        if (bundle == null) {
            Log.d(TAG, "Intent extras is null")
            finish()
            return
        }
        previous = findViewById(R.id.preview)
        cancelBtn = findViewById(R.id.cancel)
        recognizeBtn = findViewById(R.id.recognize)

        cancelBtn.setOnClickListener { onCancelPressed() }
        recognizeBtn.setOnClickListener { onRecognizePressed() }


        dataImage = bundle.getByteArray(IMAGE_FROM_CAMERA) ?: return
        dataToken = bundle.getString(TOKEN)
        val readyImage = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.size)

        previous.setImageBitmap(readyImage)
    }

    private fun onRecognizePressed() {
        if (dataToken == null) {
            Log.d(TAG, "DataToken is null")
            finish()
            return
        }

        GlobalScope.launch {
            val loaderPhoto = LoaderDataFromServer()
            val foundProduct = loaderPhoto.checkPhoto(dataToken!!, dataImage)

            runOnUiThread{
                val intent = Intent()
                val bundle = Bundle()
                if(foundProduct == null){
                    bundle.putString(FOUND_PRODUCT_NAME, null)
                    bundle.putInt(FOUND_PRODUCT_ID, -1)

                }else{
                    bundle.putString(FOUND_PRODUCT_NAME, foundProduct.productName)
                    bundle.putInt(FOUND_PRODUCT_ID, foundProduct.productId)
                }
                intent.putExtra("bundle", bundle)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun onCancelPressed() {
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}