package com.example.tk_app.classify_product.watch

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tk_app.R


class WatchAdapter  (private val products4: List<ProductWatch>) : RecyclerView.Adapter<WatchAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_Images_Show_Electronic: ImageView = view.findViewById(R.id.tv_images_show)
        val tv_Price_Show_Electronic: TextView = view.findViewById(R.id.tv_price_show)
        val tv_Name_Show_Electronic: TextView = view.findViewById(R.id.tv_name_show)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_products, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products4.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products4[position]

//        //giới hạn kí tự
//        val maxNameLength = 12
//        val truncatedName = product.name?.let { name ->
//            if (name.length > maxNameLength) {
//                "${name.substring(0, maxNameLength)}..."
//            } else {
//                name
//            }
//        } ?: ""
        Glide.with(holder.tv_Images_Show_Electronic.context)
            .load(product.imageUrl)
            .placeholder(R.drawable.baseline_person_24)
            .into(holder.tv_Images_Show_Electronic)
        holder.tv_Price_Show_Electronic.text = "${product.price} VND"
        holder.tv_Name_Show_Electronic.text = "${product.name}"

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailWatchActivity::class.java)
            val productelectronicId = product.productelectronicId // Lấy productmenId từ sản phẩm hiện tại
            intent.putExtra("productelectronicId", productelectronicId) // Truyền productmenId qua Intent
            holder.itemView.context.startActivity(intent)
        }
    }
}
