import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tk_app.R
import com.example.tk_app.classify_product.CartItemModel
import com.example.tk_app.classify_product.accessory.DetailAccessoryActivity
import com.example.tk_app.classify_product.earphones.DetailEarPhoneActivity
import com.example.tk_app.classify_product.phones.DetailProductsPhoneActivity
import com.example.tk_app.classify_product.watch.DetailWatchActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchAdapter(private val context: Context, private val productList: List<CartItemModel>) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private var searchList: List<CartItemModel> = ArrayList()

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImageView: ImageView = itemView.findViewById(R.id.productImageView)
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        val productQuantityTextView: TextView = itemView.findViewById(R.id.productQuantityTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false)
        return SearchViewHolder(itemView)
    }

    fun updateSearchList(searchList: List<CartItemModel>) {
        this.searchList = searchList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val product = searchList[position]
        holder.productNameTextView.text = product.name
        holder.productPriceTextView.text = "Price: $" + product.price
        holder.productQuantityTextView.text = "Quantity: " + product.quantity

        Glide.with(context)
            .load(product.imageUrl)
            .into(holder.productImageView)

        holder.itemView.setOnClickListener {
            val productId = product.productmenId

            val intent = createDetailIntent(holder.itemView.context, productId.orEmpty())

            intent?.let {
                holder.itemView.context.startActivity(it)
            }
        }
    }

    private fun createDetailIntent(context: Context, productId: String): Intent? {
        val databaseReferences = mapOf(
            "Phones" to FirebaseDatabase.getInstance().reference.child("Product").child("Classify")
                .child("Phones"),
            "Ear_Phones" to FirebaseDatabase.getInstance().reference.child("Product")
                .child("Classify").child("Ear_Phones"),
            "Accessory" to FirebaseDatabase.getInstance().reference.child("Product")
                .child("Classify").child("Accessory"),
            "Watch" to FirebaseDatabase.getInstance().reference.child("Product").child("Classify")
                .child("Watch")
        )

        for ((productType, reference) in databaseReferences) {
            reference.child(productId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val intent = when (productType) {
                            "Phones" -> Intent(
                                context,
                                DetailProductsPhoneActivity::class.java
                            ).putExtra("productmenId", productId)

                            "Ear_Phones" -> Intent(
                                context,
                                DetailEarPhoneActivity::class.java
                            ).putExtra("productphoneId", productId)

                            "Accessory" -> Intent(
                                context,
                                DetailAccessoryActivity::class.java
                            ).putExtra("productWomenId", productId)

                            "Watch" -> Intent(
                                context,
                                DetailWatchActivity::class.java
                            ).putExtra("productelectronicId", productId)

                            else -> null
                        }

                        intent?.let {
                            context.startActivity(it)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Xử lý khi có lỗi truy cập cơ sở dữ liệu
                }
            })
        }

        return null
    }

    override fun getItemCount(): Int {
        return searchList.size
    }
}
