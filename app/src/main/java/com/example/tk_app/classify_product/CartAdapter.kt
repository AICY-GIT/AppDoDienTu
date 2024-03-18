import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tk_app.R
import com.example.tk_app.classify_product.CartItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartAdapter(
    private val context: Context,
    private val productList: MutableList<CartItemModel>
) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImageView: ImageView = itemView.findViewById(R.id.productImageView)
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        val productQuantityTextView: TextView = itemView.findViewById(R.id.productQuantityTextView)
        val productDelete: ImageView = itemView.findViewById(R.id.btn_delete)
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

        // Load the product image using Glide
        Glide.with(context)
            .load(product.imageUrl)
            .into(holder.productImageView)

        holder.productDelete.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val deletedProduct = productList[adapterPosition]

                // Cập nhật cơ sở dữ liệu Firebase
                val userUID = FirebaseAuth.getInstance().currentUser?.uid
                if (userUID != null) {
                    val productId = deletedProduct.productId
                    if (productId != null) {
                        val databaseReference =
                            FirebaseDatabase.getInstance().reference.child("Cart").child("Cart_Fashion")
                                .child(userUID)

                        // Xóa sản phẩm dựa trên productId
                        databaseReference.orderByChild("productId").equalTo(productId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (productSnapshot in snapshot.children) {
                                        productSnapshot.ref.removeValue()
                                    }

                                    // Sau khi xóa sản phẩm, cập nhật RecyclerView và hiển thị thông báo
                                    productList.removeAt(adapterPosition)
                                    notifyItemRemoved(adapterPosition)

                                    Toast.makeText(
                                        context,
                                        "Xóa thành công sản phẩm: ${deletedProduct.name}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Xử lý lỗi nếu có
                                    Toast.makeText(
                                        context,
                                        "Xóa sản phẩm không thành công: ${error.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
