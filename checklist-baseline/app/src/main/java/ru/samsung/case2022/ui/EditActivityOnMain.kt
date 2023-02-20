package ru.samsung.case2022.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.proverka.model.ProductItem
import ru.samsung.case2022.databinding.ActivityEditOnMainBinding

class EditActivityOnMain: AppCompatActivity() {
    private lateinit var binding: ActivityEditOnMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditOnMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        val item: ProductItem = savedInstanceState?.get("Item") as ProductItem
        binding.remove.setOnClickListener(){onRemovePressed(item.productId)}
        binding.save.setOnClickListener(){onSavePressed()}
    }

    private fun onSavePressed() {
        val intent = Intent()
        val name = binding.editProductName.text.toString()
        val num = binding.editProductNum.text.toString()
        if (name.isNotBlank()) {
            if (num.isNotBlank()) {
                intent.putExtra("AddElementName", binding.editProductName.text.toString() )
                intent.putExtra("AddElementNum", binding.editProductNum.text.toString().toInt() )
            } else {
                intent.putExtra("AddElementName", binding.editProductName.text.toString() )
                intent.putExtra("AddElementNum", 1 )
            }
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun onRemovePressed(id: Int) {
        val intent = Intent()
        intent.putExtra("Id", id)
        setResult(RESULT_CANCELED, intent)
        finish()

    }

}