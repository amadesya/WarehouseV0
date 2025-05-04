package com.example.warehousev0.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.warehousev0.model.Product

class ProductAdapter(
    private var allProducts: List<Product>,
    private val listener: OnProductClickListener
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var filteredProducts: List<Product> = allProducts
    private var searchQuery: String = ""

    interface OnProductClickListener {
        fun onProductClick(product: Product)
    }

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = filteredProducts[position]
        val displayText = "${product.name} (${product.quantity})"

        holder.nameText.text = getHighlightedText(displayText, searchQuery)

        holder.itemView.setOnClickListener {
            listener.onProductClick(product)
        }
    }

    override fun getItemCount(): Int = filteredProducts.size

    fun filter(query: String) {
        searchQuery = query
        applyFilter()
    }

    fun updateData(newProducts: List<Product>) {
        allProducts = newProducts
        applyFilter()
    }

    private fun applyFilter() {
        filteredProducts = if (searchQuery.isBlank()) {
            allProducts
        } else {
            allProducts.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    private fun getHighlightedText(text: String, query: String): SpannableString {
        val spannable = SpannableString(text)
        if (query.isBlank()) return spannable

        val lowerText = text.lowercase()
        val lowerQuery = query.lowercase()

        val startIndex = lowerText.indexOf(lowerQuery)
        if (startIndex >= 0) {
            spannable.setSpan(
                ForegroundColorSpan(Color.RED),
                startIndex,
                startIndex + query.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannable
    }
}
