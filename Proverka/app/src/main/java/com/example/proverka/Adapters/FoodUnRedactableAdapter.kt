package com.example.proverka.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.proverka.databinding.ItemFoodUnredactableBinding
import com.example.proverka.model.ProductItem
import com.example.proverka.model.ProductStorage


typealias  AddButtonListener = (ProductItem) -> Unit

class FoodUnRedactableAdapter(
    private val products: ProductStorage

    ): BaseAdapter() {

    override fun getItem(position: Int): ProductItem? {
        return products.getProduct(position)
    }

    override fun getItemId(position: Int): Long {
        return products.getProduct(position)?.productId?.toLong()!!
    }

    override fun getCount(): Int {
        return products.getSize()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemFoodUnredactableBinding =
            convertView?.tag as ItemFoodUnredactableBinding? ?:
            createUnBinding(parent.context)

        val product: ProductItem = getItem(position) ?: return binding.root

        binding.foodName.text = product.productName
        binding.foodNum.text = product.productQuantity.toString()

        return binding.root
    }

    private fun createUnBinding(context: Context): ItemFoodUnredactableBinding {
        val binding:ItemFoodUnredactableBinding = ItemFoodUnredactableBinding.inflate(LayoutInflater.from(context))
        binding.root.tag = binding
        return binding
    }
}