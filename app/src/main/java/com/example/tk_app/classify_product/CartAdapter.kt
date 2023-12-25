import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tk_app.R
import com.example.tk_app.classify_product.CartItem

class CartAdapter(private val context: Context, private val productList: List<CartItem>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImageView: ImageView = itemView.findViewById(R.id.productImageView)
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        val productQuantityTextView: TextView = itemView.findViewById(R.id.productQuantityTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = productList[position]
        holder.productNameTextView.text = product.name
        holder.productPriceTextView.text = "Price: $" + product.price
        holder.productQuantityTextView.text = "Quantity: " + product.quantity

        // Load the product image using Glide (you need to import the Glide library)
        Glide.with(context)
            .load(product.imageUrl)
            .into(holder.productImageView)
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}