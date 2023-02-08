package com.example.proverka

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.proverka.Adapters.FoodRedactableAdapter
import com.example.proverka.databinding.ActivityEditBinding
import com.example.proverka.model.FoodItem
import com.example.proverka.model.FoodList

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
        }
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

    }

    private fun onIncPressed(it: FoodItem) {
        it.name?.let { it1 -> data.incFood(it1) }
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