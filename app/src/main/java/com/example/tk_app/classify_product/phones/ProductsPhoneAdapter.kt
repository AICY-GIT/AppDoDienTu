package com.example.tk_app.classify_product.phones

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tk_app.R

class ProductsPhoneAdapter(private val products: List<ProductPhone>) : RecyclerView.Adapter<ProductsPhoneAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_Images_Show_Men: ImageView = view.findViewById(R.id.tv_images_show_men)
        val tv_Price_Show_Men: TextView = view.findViewById(R.id.tv_price_show_men)
        val tv_Name_Show_Men: TextView = view.findViewById(R.id.tv_name_show_men)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_products_phone, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        //giới hạn kí tự
        val maxNameLength = 12
        val truncatedName = product.name?.let { name ->
            if (name.length > maxNameLength) {
                "${name.substring(0, maxNameLength)}..."
            } else {
                name
            }
        } ?: ""

        Glide.with(holder.tv_Images_Show_Men.context)
            .load(product.imageUrl)
            .placeholder(R.drawable.baseline_person_24)
            .into(holder.tv_Images_Show_Men)
        holder.tv_Price_Show_Men.text = "${product.price} VND"
        holder.tv_Name_Show_Men.text = "${truncatedName}"

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailProductsPhoneActivity::class.java)
            val productmenId = product.productmenId // Lấy productmenId từ sản phẩm hiện tại
            intent.putExtra("productmenId", productmenId) // Truyền productmenId qua Intent
            holder.itemView.context.startActivity(intent)
        }
    }
}
