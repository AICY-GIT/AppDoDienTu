package com.example.tk_app.classify_product

import CartAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tk_app.MainActivity
import com.example.tk_app.R
import com.example.tk_app.account.LoginActivity
import com.example.tk_app.fragment.HomeFragment
import com.example.tk_app.pay.PurchaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.math.BigDecimal

// Import các thư viện và class cần thiết

class CartActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private val productList: MutableList<CartItem> = ArrayList()
    var totalCartPrice: BigDecimal = BigDecimal.ZERO
    lateinit var btnBuy: Button
    lateinit var btnReturn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        btnBuy = findViewById<Button>(R.id.btnBuy)
        btnReturn= findViewById(R.id.btn_return_cart)
        btnReturn.setOnClickListener{
            val intent = Intent(this@CartActivity, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
        btnBuy.setOnClickListener {
            // Tạo intent và đưa dữ liệu vào intent
            val intent = Intent(this, PurchaseActivity::class.java)
            intent.putExtra("totalCartPrice", totalCartPrice.toString()) // Tổng giá giỏ hàng
            intent.putParcelableArrayListExtra(
                "productList",
                ArrayList(productList)
            ) // Danh sách sản phẩm trong giỏ hàng
            startActivity(intent)
        }
            // Kiểm tra người dùng hiện tại từ Firebase Authentication
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Đóng màn hình giỏ hàng
        } else {
            recyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            cartAdapter = CartAdapter(this, productList)
            recyclerView.adapter = cartAdapter

            // Lấy ID người dùng hiện tại từ Firebase Authentication
            val userUID = FirebaseAuth.getInstance().currentUser?.uid

            if (userUID != null) {
                val databaseReference =
                    FirebaseDatabase.getInstance().reference.child("Cart").child("Cart_Fashion")
                        .child(userUID)
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        productList.clear()
                        totalCartPrice = BigDecimal.ZERO // Khởi tạo tổng giá


                        for (productSnapshot in snapshot.children) {
                            val product = productSnapshot.getValue(CartItem::class.java)
                            val productStatus = productSnapshot.child("status").getValue(String::class.java)

                            if (product != null && productStatus == "wait") {
                                productList.add(product)

                                // Xử lý chuỗi giá trị từ Firebase
                                val priceWithoutDots = product.price?.replace(".", "") ?: "0"

                                // Chuyển đổi giá sản phẩm thành BigDecimal
                                val productPrice = try {
                                    BigDecimal(priceWithoutDots)
                                } catch (e: NumberFormatException) {
                                    BigDecimal.ZERO // Hoặc giá trị mặc định khác tùy thuộc vào yêu cầu của bạn
                                }

                                // Chuyển đổi quantity từ String sang Int
                                val productQuantity = product.quantity?.toIntOrNull() ?: 0

                                val productTotalPrice = productPrice * BigDecimal(productQuantity.toString())
                                totalCartPrice = totalCartPrice.add(productTotalPrice)
                            }
                        }


                        cartAdapter.notifyDataSetChanged()

                        // Cập nhật tổng giá trên giao diện
                        val totalPriceTextView = findViewById<TextView>(R.id.totalPriceTextView)
                        totalPriceTextView.text = "Tổng tiền: $$totalCartPrice"
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Xử lý lỗi nếu cần
                    }
                })
            }
        }
    }
}