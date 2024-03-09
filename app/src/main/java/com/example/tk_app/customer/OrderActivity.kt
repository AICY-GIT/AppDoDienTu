package com.example.tk_app.customer

import OrderAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tk_app.MainActivity
import com.example.tk_app.R
import com.example.tk_app.account.LoginActivity
import com.example.tk_app.pay.Payment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private val orderList: MutableList<Payment> = ArrayList()
    lateinit var btnReturn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        btnReturn = findViewById(R.id.btn_return_order)
        btnReturn.setOnClickListener {
            val intent = Intent(this@OrderActivity, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Đóng màn hình giỏ hàng
        } else {
            recyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            orderAdapter = OrderAdapter(this, orderList)
            recyclerView.adapter = orderAdapter

            // Lấy ID người dùng hiện tại từ Firebase Authentication
            val userUID = FirebaseAuth.getInstance().currentUser?.uid


            if (userUID != null) {
                val databaseReference =
                    FirebaseDatabase.getInstance().reference.child("Payments")
                        .child(userUID)
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(Payment::class.java)
                            if (order != null) {
                                orderList.add(order)
                            }
                        }
                        orderAdapter.notifyDataSetChanged()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Xử lý lỗi nếu cần
                    }
                })
            }
        }
    }
}