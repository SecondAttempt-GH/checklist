package ru.samsung.case2022.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proverka.model.ProductItem
import ru.samsung.case2022.R
import ru.samsung.case2022.databinding.ActivityEditOnMainBinding


private const val TAG = "EditActivityOnMain"

class EditActivityOnMain : AppCompatActivity() {
    private lateinit var binding: ActivityEditOnMainBinding
    private lateinit var productItem: ProductItem

    private lateinit var productNameText: TextView
    private lateinit var productCountInt: TextView

    private lateinit var saveChangesBtn: Button
    private lateinit var removeChangesBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_on_main)

        val bundle = intent.getBundleExtra("bundle")
        if (bundle == null) {
            Log.d(TAG, "Intent extras is null")
            finish()
            return
        }
        val rawProduct = bundle.getParcelable<ProductItem>(PRODUCT)
        if (rawProduct == null) {
            finish()
            return
        }
        productNameText = findViewById(R.id.editProductName)
        productCountInt = findViewById(R.id.editProductNum)
        saveChangesBtn = findViewById(R.id.save)
        removeChangesBtn = findViewById(R.id.remove)


        productItem = rawProduct

        productNameText.text = productItem.productName
        productCountInt.text = productItem.productQuantity.toString()

        removeChangesBtn.setOnClickListener() { onRemovePressed(productItem.productId) }
        saveChangesBtn.setOnClickListener() { onSavePressed() }
    }

    private fun onSavePressed() {
        val intent = Intent()
        val name = productNameText.text.toString()
        val num = productCountInt.text.toString()
        val bundle = Bundle()
        if (name.isNotBlank()) {
            if (num.isNotBlank()) {
                bundle.putString(PRODUCT_NAME_EDIT, productNameText.text.toString())
                bundle.putInt(PRODUCT_QUANTITY_EDIT, productCountInt.text.toString().toInt())
            } else {
                bundle.putString(PRODUCT_NAME_EDIT, productNameText.text.toString())
                bundle.putInt(PRODUCT_QUANTITY_EDIT, 1)
            }
            bundle.putInt(PRODUCT_ID, productItem.productId)
        }
        intent.putExtra("bundle", bundle)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun onRemovePressed(id: Int) {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putInt(PRODUCT_ID, id)
        intent.putExtra("bundle", bundle)
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}