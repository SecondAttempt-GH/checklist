package com.example.proverka.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.proverka.model.ProductItem
import com.example.proverka.model.ProductStorage
import ru.samsung.case2022.databinding.ItemFoodUnredactableBinding
import ru.samsung.case2022.ui.AddActivity
import ru.samsung.case2022.ui.EditActivityOnMain

private const val EDIT_REQUEST_CODE = 5
typealias  AddButtonListener = (ProductItem) -> Unit

class FoodUnRedactableAdapter(
    private val context: Context,
    private val onPressedListener: OnPressedListener
): RecyclerView.Adapter<FoodUnRedactableAdapter.FoodUnRedactableViewHolder>(), View.OnClickListener {

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
        val holder = FoodUnRedactableViewHolder(binding)
        return holder
    }

    override fun onBindViewHolder(holder: FoodUnRedactableViewHolder, position: Int) {
        val productItem = products.getProduct(position)
        with(holder.binding) {
            productName.text = productItem!!.productName
            foodNum.text = productItem!!.productQuantity.toString()
            productName.tag = productItem
        }
        holder.binding.productName.setOnClickListener(this)
    }

    override fun getItemCount(): Int {
        return products.getSize()
    }

    override fun onClick(v: View) {
        onPressedListener.invoke(v)
    }
}