package com.example.tk_app

import OrderDetailsModel
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tk_app.R

class OrderDetailAdapter(private val context: Context, private val productList: List<OrderDetailsModel>) :
    RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {

    inner class OrderDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        val productQuantityTextView: TextView = itemView.findViewById(R.id.productQuantityTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false)
        return OrderDetailViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        val product = productList[position]

        // TODO: Load the product image from your data source (e.g., Firebase Storage)
        // Example: If you have a field like "imagePath" in OrderDetailsModel
        // val imagePath = product.imagePath
        // Load image using Glide or another image loading library

        // Display product information
        holder.productNameTextView.text = "Product: ${product.productId}"
        holder.productPriceTextView.text = "Price: $${product.price}"
        holder.productQuantityTextView.text = "Quantity: ${product.amount}"
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
