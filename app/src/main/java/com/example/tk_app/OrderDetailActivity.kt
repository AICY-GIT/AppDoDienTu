package com.example.tk_app

import OrderDetailAdapter
import OrderDetailsModel
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tk_app.account.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderDetailAdapter
    private val orderList: MutableList<OrderDetailsModel> = ArrayList()
    private lateinit var btnReturn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        btnReturn = findViewById(R.id.btn_return_order)
        btnReturn.setOnClickListener {
            val intent = Intent(this@OrderDetailActivity, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close the shopping cart screen
        } else {
            recyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            orderAdapter = OrderDetailAdapter(this, orderList, FirebaseDatabase.getInstance().reference.child("Product"))
            recyclerView.adapter = orderAdapter

            // Get the ID of the current user from Firebase Authentication
            val userUID = FirebaseAuth.getInstance().currentUser?.uid

            if (userUID != null) {
                val databaseReference =
                    FirebaseDatabase.getInstance().reference.child("OrderDetails")
                        .child(userUID)
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        orderList.clear() // Clear the list before adding new data
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(OrderDetailsModel::class.java)
                            if (order != null) {
                                orderList.add(order)
                            }
                        }
                        orderAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle errors if needed
                    }
                })
            }
        }
    }
}
