package com.example.proverka.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.proverka.databinding.ItemFoodReadableBinding
import com.example.proverka.handlers.QueueOfProductsForUpdating
import com.example.proverka.model.ProductItem
import com.example.proverka.model.ProductStorage


typealias OnPressedListener = (View) -> Unit

class ClickableProductAdapter(
    private val products: ProductStorage,
    private val onPressedListener: OnPressedListener
) : BaseAdapter(), View.OnClickListener {
    override fun getCount(): Int {
        return products.getSize()
    }

    override fun getItem(position: Int): ProductItem? {
        return products.getProduct(position)
    }

    override fun getItemId(position: Int): Long {
        return products.getProduct(position)?.productId?.toLong() ?: 0
    }

    override fun notifyDataSetChanged() {
        val notRemovedProducts = products.getProducts().filter { p -> !p.isRemoved() }
        products.updateProducts(notRemovedProducts as ArrayList<ProductItem>)
        super.notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemFoodReadableBinding =
            convertView?.tag as ItemFoodReadableBinding? ?: createBinding(parent.context)

        val product: ProductItem = getItem(position) ?: return binding.root

        binding.titelTextViewName.text = product.productName
        binding.titelTextViewNum.text = product.productQuantity.toString()
        binding.dellbtt.tag = product
        binding.incbtt.tag = product
        binding.disbtt.tag = product
        binding.titelTextViewName.tag = product

        return binding.root
    }

    private fun createBinding(context: Context): ItemFoodReadableBinding {
        val binding: ItemFoodReadableBinding =
            ItemFoodReadableBinding.inflate(LayoutInflater.from(context))
        binding.dellbtt.setOnClickListener(this)
        binding.incbtt.setOnClickListener(this)
        binding.disbtt.setOnClickListener(this)
        binding.titelTextViewName.setOnClickListener(this)
        binding.root.tag = binding
        return binding
    }

    override fun onClick(v: View) {
        onPressedListener.invoke(v)
    }
}