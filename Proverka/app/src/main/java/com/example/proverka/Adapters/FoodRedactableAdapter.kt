package com.example.proverka.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.proverka.R
import com.example.proverka.databinding.ItemFoodReadableBinding
import com.example.proverka.model.FoodItem
import com.example.proverka.model.FoodList


typealias OnPressedListener = (View) -> Unit

class FoodRedactableAdapter(
    private val foods: FoodList,
    private val onPressedListener: OnPressedListener


    ): BaseAdapter(), View.OnClickListener{
    override fun getCount(): Int {
        return foods.getSize()
    }

    override fun getItem(position: Int): FoodItem {
        return foods.getItem(position)
    }

    override fun getItemId(position: Int): Long {
        return foods.getItem(position).id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemFoodReadableBinding =
            convertView?.tag as ItemFoodReadableBinding? ?:
            createBinding(parent.context)

        val food:FoodItem = getItem(position)

        binding.titelTextViewName.text = food.name
        binding.titelTextViewNum.text = food.num.toString()
        binding.dellbtt.tag = food
        binding.incbtt.tag = food
        binding.disbtt.tag = food
        binding.titelTextViewName.tag = food

        return binding.root

    }



    private fun createBinding(context: Context): ItemFoodReadableBinding {
        val binding:ItemFoodReadableBinding = ItemFoodReadableBinding.inflate(LayoutInflater.from(context))
        binding.dellbtt.setOnClickListener(this)
        binding.incbtt.setOnClickListener(this)
        binding.disbtt.setOnClickListener(this)
        binding.titelTextViewName.setOnClickListener(this)
        binding.root.tag = binding
        return binding
    }

    override fun onClick(v: View){
        onPressedListener.invoke(v)
    }
}