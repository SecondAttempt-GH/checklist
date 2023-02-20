package com.example.proverka.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.proverka.model.ProductItem
import com.example.proverka.model.ProductStorage
import ru.samsung.case2022.databinding.ItemFoodUnredactableBinding


typealias  AddButtonListener = (ProductItem) -> Unit

class FoodUnRedactableAdapter(): RecyclerView.Adapter<FoodUnRedactableAdapter.FoodUnRedactableViewHolder>() {


    var products = ProductStorage()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    class FoodUnRedactableViewHolder(
        val binding: ItemFoodUnredactableBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodUnRedactableViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFoodUnredactableBinding.inflate(inflater, parent, false)
        return FoodUnRedactableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodUnRedactableViewHolder, position: Int) {
        val productItem = products.getProduct(position)
        with(holder.binding) {
            productName.text = productItem!!.productName
            foodNum.text = productItem!!.productQuantity.toString()
        }
    }

    override fun getItemCount(): Int {
        return products.getSize()
    }
//    override fun getItem(position: Int): ProductItem? {
//        return products.getProduct(position)
//    }

//    override fun getItemId(position: Int): Long {
//        return products.getProduct(position)?.productId?.toLong()!!
//    }



//    override fun getCount(): Int {
//        return products.getSize()
//    }

//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val binding: ItemFoodUnredactableBinding =
//            convertView?.tag as ItemFoodUnredactableBinding? ?:
//            createUnBinding(parent.context)
//
//        val product: ProductItem = getItem(position) ?: return binding.root
//
//        binding.productName.text = product.productName
//        binding.foodNum.text = product.productQuantity.toString()
//
//        return binding.root
//    }

    private fun createUnBinding(context: Context): ItemFoodUnredactableBinding {
        val binding:ItemFoodUnredactableBinding = ItemFoodUnredactableBinding.inflate(LayoutInflater.from(context))
        binding.root.tag = binding
        return binding
    }
}