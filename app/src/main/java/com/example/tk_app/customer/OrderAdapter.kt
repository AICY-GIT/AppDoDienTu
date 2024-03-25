import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tk_app.OrderModel
import com.example.tk_app.R
import com.google.firebase.database.*

class OrderAdapter(private val context: Context, private val orderList: List<OrderModel>) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val product_image_admin: ImageView = itemView.findViewById(R.id.iv_product_image_admin)
        val order_name_admin: TextView = itemView.findViewById(R.id.tv_order_name_admin)
        val tv_order_date_admin: TextView = itemView.findViewById(R.id.tv_order_date_admin)
        val tv_order_total_price_admin: TextView = itemView.findViewById(R.id.tv_order_total_price_admin)
        val tv_order_address_admin: TextView = itemView.findViewById(R.id.tv_order_address_admin)
        val tv_order_payment_admin: TextView = itemView.findViewById(R.id.tv_order_payment_admin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.order_item_admin, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderList[position]
        holder.tv_order_date_admin.text = "Ngày đặt mua: " + order.orderDate
        holder.tv_order_total_price_admin.text = "Tổng số tiền: " + order.totalPrice
        holder.tv_order_address_admin.text = "Địa chỉ: "+ order.shippingAddress
        holder.tv_order_payment_admin.text = "Phương thức thanh toán: "+order.paymentMethod

        val userId = order.userId // Lấy userId từ đối tượng OrderModel

         //Kiểm tra userId có tồn tại và thực hiện lấy thông tin người dùng từ Realtime Database
        if (!userId.isNullOrEmpty()) {
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
            val userRef: DatabaseReference = databaseReference.child("Account").child("User").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userName = snapshot.child("Name").value.toString()
                        val userImageUrl = snapshot.child("Image").value.toString()

                        // Cập nhật thông tin người dùng vào ViewHolder
                        holder.order_name_admin.text = userName
                        Glide.with(context).load(userImageUrl).into(holder.product_image_admin)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Xử lý lỗi khi truy vấn dữ liệu người dùng
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}
