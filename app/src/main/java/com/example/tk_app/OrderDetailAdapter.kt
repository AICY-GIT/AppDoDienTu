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
import com.google.firebase.database.*

class OrderDetailAdapter(
    private val context: Context,
    private val productList: List<OrderDetailsModel>,
    private val databaseReference: DatabaseReference // Reference to your Firebase Database
) : RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {

    inner class OrderDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_order_ID: TextView = itemView.findViewById(R.id.tv_order_ID)
        val tv_order_total_price: TextView = itemView.findViewById(R.id.tv_order_total_price)
        val tv_order_address: TextView = itemView.findViewById(R.id.tv_order_address)
        val tv_order_payment: TextView = itemView.findViewById(R.id.tv_order_payment)
        val iv_product_image: ImageView = itemView.findViewById(R.id.iv_product_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false)
        return OrderDetailViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        val product = productList[position]

        holder.tv_order_ID.text = product.orderDate
        holder.tv_order_total_price.text = product.price
        holder.tv_order_payment.text = product.amount

        // Fetch product details from Firebase based on productId
        val productId = product.productId
        if (productId != null) {
            val classifyRef =
                FirebaseDatabase.getInstance().reference.child("Product").child("Classify")

            classifyRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var found = false
                    for (classifySnapshot in snapshot.children) {
                        val productSnapshot = classifySnapshot.child(productId)
                        if (productSnapshot.exists()) {
                            val productName = productSnapshot.child("name").getValue(String::class.java)
                            val productImage = productSnapshot.child("imageUrl").getValue(String::class.java)

                            // Kiểm tra xem dữ liệu có tồn tại hay không trước khi gán
                            if (productName != null && productImage != null) {
                                holder.tv_order_address.text = productName
                                Glide.with(context).load(productImage).into(holder.iv_product_image)
                                found = true
                                break
                            }
                        }
                    }
                    if (!found) {
                        // Xử lý trường hợp không tìm thấy productId
                        Toast.makeText(context, "Product ID not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Xử lý lỗi cơ sở dữ liệu
                    Toast.makeText(context, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Xử lý trường hợp productId là null
            Toast.makeText(context, "Product ID is null", Toast.LENGTH_SHORT).show()
        }
    }



    override fun getItemCount(): Int {
        return productList.size
    }
}
