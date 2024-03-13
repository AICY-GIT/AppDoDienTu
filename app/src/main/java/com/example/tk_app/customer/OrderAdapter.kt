import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tk_app.OrderDetailActivity
import com.example.tk_app.OrderModel
import com.example.tk_app.R
import com.example.tk_app.classify_product.accessory.DetailAccessoryActivity
import com.example.tk_app.pay.Payment

class OrderAdapter(private val context: Context, private val orderList: List<OrderModel>) :
    RecyclerView.Adapter<OrderAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_order_ID: TextView = itemView.findViewById(R.id.tv_order_ID)
        val tv_order_total_price: TextView = itemView.findViewById(R.id.tv_order_total_price)
        val tv_order_address: TextView = itemView.findViewById(R.id.tv_order_address)
        val tv_order_payment: TextView = itemView.findViewById(R.id.tv_order_payment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false)
        return CartViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val order = orderList[position]
        holder.tv_order_ID.text = order.orderDate
        holder.tv_order_total_price.text = "Total Price: $" + order.totalPrice
        holder.tv_order_address.text = order.shippingAddress
        holder.tv_order_payment.text = order.paymentMethod

//        holder.itemView.setOnClickListener {
//            val intent = Intent(holder.itemView.context, OrderDetailActivity::class.java)
//            val orderId = order.orderId
//            intent.putExtra("orderId", orderId)
//            holder.itemView.context.startActivity(intent)
//        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}