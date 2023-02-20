package ru.samsung.case2022.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proverka.Adapters.ClickableProductAdapter
import com.example.proverka.handlers.QueueOfProductsForUpdating
import com.example.proverka.model.ProductItem
import com.example.proverka.model.ProductStorage
import ru.samsung.case2022.R
import ru.samsung.case2022.databinding.ActivityEditBinding
import ru.samsung.case2022.databinding.DialogEditNameBinding


class EditActivity : AppCompatActivity(){

    private lateinit var binding: ActivityEditBinding

    private lateinit var data: ProductStorage
    private lateinit var redactableAdapter: ClickableProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater).also { setContentView(it.root) }

        supportActionBar?.title = "Редактирование списка"
        binding.backButton.setOnClickListener { onBackBttPressed() }
        binding.ok.setOnClickListener {onOkPressed()}
        data = ProductStorage()
        (savedInstanceState?.getParcelableArrayList<ProductItem>(KEY_DATA) ?: intent.getParcelableArrayListExtra(FOOD_LIST))?.let {
            data.updateProducts(it)
        }
        setupList()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(KEY_DATA, data.getProducts())
    }

    private fun onBackBttPressed() {
        finish()
    }


    private fun setupList(){
        redactableAdapter = ClickableProductAdapter(data) {onPressed(it)}

        binding.FoodListView.adapter = redactableAdapter
    }


    //// тут происходят изменения
    private fun onPressed(v: View) {
        val product = v.tag as ProductItem
        when (v.id) {
            R.id.remove -> {
                data.remove(product.productId)
                QueueOfProductsForUpdating.addChangedProduct(product)
            }
            R.id.incbtt -> {
                data.addOnceProduct(product.productId)
                QueueOfProductsForUpdating.addChangedProduct(product)
            }
            R.id.disbtt -> {
                data.removeOnceProduct(product.productId)
                QueueOfProductsForUpdating.addChangedProduct(product)
            }
            R.id.titelTextViewName -> {
                val dialogBinding = DialogEditNameBinding.inflate(layoutInflater)
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Изменить имя")
                    .setView(dialogBinding.root)
                    .setPositiveButton("Ok") { d, which ->
                        val newNameProduct = dialogBinding.editProductName2.text.toString()
                        if (newNameProduct.isNotBlank()) {
                            data.editNameProduct(product.productId, newNameProduct)
                            QueueOfProductsForUpdating.addChangedProduct(product)
                        }

                    }.create()
                dialog.show()
            }
        }
        redactableAdapter.notifyDataSetChanged()
    }

    private fun onOkPressed(){
        val intent = Intent()
        intent.putExtra(FOOD_LIST, data.getProducts())
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        @JvmStatic val FOOD_LIST = "FOOD_LIST"
        @JvmStatic private val KEY_DATA = "Data"
    }
}