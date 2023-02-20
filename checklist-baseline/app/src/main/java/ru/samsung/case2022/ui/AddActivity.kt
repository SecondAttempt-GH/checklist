package ru.samsung.case2022.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.samsung.case2022.databinding.ActivityAddBinding

class AddActivity : AppCompatActivity(){
    private lateinit var binding: ActivityAddBinding
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater).also { setContentView(it.root) }
        binding.save.setOnClickListener(){onSavePrassed()}
    }

    private fun onSavePrassed() {
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
}