package com.example.proverka

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proverka.Adapters.FoodRedactableAdapter
import com.example.proverka.databinding.ActivityEditBinding
import com.example.proverka.databinding.DialogEditNameBinding
import com.example.proverka.model.FoodItem
import com.example.proverka.model.FoodList

// Экран изменений

class EditActivity : AppCompatActivity(){

    private lateinit var binding: ActivityEditBinding

    private lateinit var data: FoodList
    private lateinit var redactableAdapter: FoodRedactableAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater).also { setContentView(it.root) }

        binding.backButton.setOnClickListener { onBackBttPressed() }
        binding.okButton.setOnClickListener {onOkPressed()}
        data = FoodList()
        (savedInstanceState?.getParcelableArrayList<FoodItem>(KEY_DATA) ?: intent.getParcelableArrayListExtra(FOOD_LIST))?.let {
            data.setFood(it)
        }// savedInstanceState?.getParcelableArrayList<FoodItem>(KEY_DATA) это данные которые пришли из Main
        //intent.getParcelableArrayListExtra(FOOD_LIST)) это данные нанешней активити сделана хуй знает зачам, но так в гайде было написано, вроде просто позволяет не потерять изменения если данная активити каким-то образом вдруг решила перезапуститься
        setupList()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(KEY_DATA, data.getFoods())
    }

    private fun onBackBttPressed() {
        finish()
    }


    private fun setupList(){
        redactableAdapter = FoodRedactableAdapter(data) {onPressed(it)}

        binding.FoodListView.adapter = redactableAdapter
    }


    //// тут происходят изменения буть окуратен они применяются только если нажата кнопка ОК (метод onOkPressed())
    private fun onPressed(v: View) {
        val food = v.tag as FoodItem
        if (v.id == R.id.dellbtt) {
            data.remove(food)
            redactableAdapter.notifyDataSetChanged()
            return
        }
        if (v.id == R.id.incbtt) {
            data.incFood(food)
            redactableAdapter.notifyDataSetChanged()
            return
        }
        if (v.id == R.id.disbtt) {
            data.decFood(food)
            redactableAdapter.notifyDataSetChanged()
            return
        }
        if (v.id == R.id.titelTextViewName) {
            val dialogBinding = DialogEditNameBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(this)
                .setTitle("Edit Name")
                .setView(dialogBinding.root)
                .setPositiveButton("Ok") { d, which ->
                    val name = dialogBinding.editTextTextFoodNameDial.text.toString()
                    if (name.isNotBlank()) {
                        data.editName(food, name)
                    }

                }.create()
            dialog.show()
            redactableAdapter.notifyDataSetChanged()
            return


        }

    }

    private fun onIncPressed(it: FoodItem) {
        it.name?.let { it1 -> data.incFood(it1) } /// сам уже не помню почему it1 но это функции которые тригерятся при нажатии на кнопки элемента списка. они вызывают метод onPressed и кладут в него Viev на который ты нажал, а в этом View в параметре tag какой это элемент в списке продуктов
        redactableAdapter.notifyDataSetChanged()
    }

    private fun onDecPressed(it: FoodItem) {
        it.name?.let { it1 -> data.decFood(it1) }
        redactableAdapter.notifyDataSetChanged()
    }

    private fun onOkPressed(){
        val intent = Intent()
        intent.putExtra(FOOD_LIST, data.getFoods())
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
        @JvmStatic val EXTRA_EDIT = "EXTRA_EDIT"
        @JvmStatic val Result_OK = 1
    }
}