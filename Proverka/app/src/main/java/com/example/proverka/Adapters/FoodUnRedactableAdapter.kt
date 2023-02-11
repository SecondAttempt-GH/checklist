package com.example.proverka.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.proverka.databinding.ItemFoodUnredactableBinding
import com.example.proverka.model.FoodItem
import com.example.proverka.model.FoodList


typealias  AddButtonListener = (FoodItem) -> Unit

class FoodUnRedactableAdapter(
    private val foods: FoodList

    ): BaseAdapter() {

    override fun getItem(position: Int): FoodItem {
        return foods.getItem(position)
    }

    override fun getItemId(position: Int): Long {
        return foods.getItem(position).id
    }

    override fun getCount(): Int {
        return foods.getSize()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemFoodUnredactableBinding =
            convertView?.tag as ItemFoodUnredactableBinding? ?:
            createUnBinding(parent.context)

        val food:FoodItem = getItem(position)

        binding.foodName.text = food.name
        binding.foodNum.text = food.num.toString()

        return binding.root

    }

    private fun createUnBinding(context: Context): ItemFoodUnredactableBinding {
        val binding:ItemFoodUnredactableBinding = ItemFoodUnredactableBinding.inflate(LayoutInflater.from(context))
        binding.root.tag = binding
        return binding
    }
}