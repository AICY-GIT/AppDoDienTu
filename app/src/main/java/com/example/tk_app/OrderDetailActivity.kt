package com.example.tk_app

import OrderDetailsModel
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tk_app.account.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var orderDetailAdapter: OrderDetailAdapter
    private val productList: MutableList<OrderDetailsModel> = ArrayList()
    private lateinit var btnReturn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        // Đọc dữ liệu từ Intent
        val orderId = intent.getStringExtra("orderId")

        btnReturn = findViewById(R.id.btn_return_order)
        btnReturn.setOnClickListener {
            val intent = Intent(this@OrderDetailActivity, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
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
            orderDetailAdapter = OrderDetailAdapter(this, productList)
            recyclerView.adapter = orderDetailAdapter

            // Lấy ID người dùng hiện tại từ Firebase Authentication
            val userUID = FirebaseAuth.getInstance().currentUser?.uid
            if (userUID != null) {
                val databaseReference =
                    FirebaseDatabase.getInstance().reference.child("OrderDetails").child(userUID)

                // Thêm listener để lắng nghe sự thay đổi trong danh sách orderDetails
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        productList.clear() // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                        for (orderDetailsSnapshot in dataSnapshot.children) {
                            val orderDetailsId = orderDetailsSnapshot.key

                            // Kiểm tra xem orderId có trong danh sách orderdetailsId hay không
                            if (orderDetailsId == orderId) {
                                val orderDetailsModel = orderDetailsSnapshot.getValue(OrderDetailsModel::class.java)
                                if (orderDetailsModel != null) {
                                    productList.add(orderDetailsModel)
                                }
                            }
                            else{
                                Toast.makeText(
                                this@OrderDetailActivity,
                                orderId + "\n" + orderDetailsId,
                                Toast.LENGTH_SHORT
                            ).show()
                            }
                        }
                        // Thông báo adapter rằng dữ liệu đã thay đổi
                        orderDetailAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Xử lý lỗi nếu có
                    }
                })
            }

        }
    }
}
