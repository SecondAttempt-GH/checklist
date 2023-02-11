package com.example.proverka

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.example.proverka.Adapters.FoodUnRedactableAdapter
import com.example.proverka.databinding.ActivityMainBinding
import com.example.proverka.databinding.PrintFoodBinding
import com.example.proverka.model.FoodItem
import com.example.proverka.model.FoodList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var data: FoodList
    private lateinit var unredadapter: FoodUnRedactableAdapter


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
            print("пришло")
        }
        unredadapter.notifyDataSetChanged()
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

    private fun setupReadableList(){




    }

    companion object{
        @JvmStatic val FOOD_LIST = "FOOD_LIST"
        @JvmStatic val FOOD_TITLE = "FOOD_TITLE"
        @JvmStatic val EDIT_REQUES_CODE = 1
    }
}