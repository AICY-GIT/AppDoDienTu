package com.example.tk_app.pay

import CartAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tk_app.MainActivity
import com.example.tk_app.R
import com.example.tk_app.account.User
import com.example.tk_app.classify_product.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PurchaseActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var totalPriceTextView: TextView
    private lateinit var productListTextView: TextView
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var userPhoneTextView: TextView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var shippingAddressEditText: EditText
    private lateinit var cashOnDeliveryCheckBox: CheckBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)

        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        productListTextView = findViewById(R.id.productListTextView)
        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            savePaymentInfoToFirebase()
        }
        // Receive data from Intent
        val totalCartPrice = intent.getStringExtra("totalCartPrice")
        val productList = intent.getParcelableArrayListExtra<CartItem>("productList")

        if (productList != null) {
            totalPriceTextView.text = "Total Price: $$totalCartPrice"

            recyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            cartAdapter = CartAdapter(this, productList)
            recyclerView.adapter = cartAdapter

            var productsText = "Product List:\n"
            for (product in productList) {
                productsText += "${product?.name} - ${product?.price} - ${product?.quantity}\n"
            }
            productListTextView.text = productsText
        } else {
            // Handle the case when productList is null
        }
        userNameTextView = findViewById(R.id.userNameTextView)
        userEmailTextView = findViewById(R.id.userEmailTextView)
        userPhoneTextView = findViewById(R.id.userPhoneTextView)
        shippingAddressEditText = findViewById(R.id.shippingAddressEditText)
        cashOnDeliveryCheckBox = findViewById(R.id.cashOnDeliveryCheckBox)

        // Tham chiếu đến Firebase Database và lấy thông tin người dùng
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().reference.child("Account/User").child(userID!!)

        // Lắng nghe sự thay đổi dữ liệu từ Firebase Database
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("Name").value.toString()
                val userEmail = snapshot.child("Email").value.toString()
                val userPhone = snapshot.child("Phone").value.toString()

                // Hiển thị thông tin người dùng trên TextView tương ứng
                userNameTextView.text = "Họ tên: $userName"
                userEmailTextView.text = "Email: $userEmail"
                userPhoneTextView.text = "Phone: $userPhone"
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra khi truy xuất dữ liệu từ Firebase
            }
        })
        cashOnDeliveryCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Xử lý khi người dùng chọn thanh toán khi nhận hàng
            } else {
                // Xử lý khi người dùng không chọn thanh toán khi nhận hàng
            }
        }
    }
    private fun savePaymentInfoToFirebase() {
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val shippingAddress = shippingAddressEditText.text.toString()
        val isCashOnDeliveryChecked = cashOnDeliveryCheckBox.isChecked

        if (shippingAddress.isBlank() || !isCashOnDeliveryChecked) {
            val errorMessage = when {
                shippingAddress.isBlank() && !isCashOnDeliveryChecked -> "Hãy nhập địa chỉ và chọn phương thức thanh toán!"
                shippingAddress.isBlank() -> "Hãy nhập địa chỉ thanh toán!"
                else -> "Bạn phải chọn phương thức thanh toán!"
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            return // Dừng quá trình lưu nếu không đủ điều kiện
        }

        // Lưu thông tin thanh toán
        val paymentRef = FirebaseDatabase.getInstance().reference.child("Payments").child(userID!!)
        val paymentInfo = Payment(
            totalPriceTextView.text.toString(),
            shippingAddress,
            isCashOnDeliveryChecked
        )
        paymentRef.setValue(paymentInfo)
            .addOnSuccessListener {
                val cartReference = FirebaseDatabase.getInstance().reference.child("Cart").child("Cart_Fashion").child(userID!!)
                // Update status to 'complete' in the cart
                cartReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (productSnapshot in dataSnapshot.children) {
                            val productStatus = productSnapshot.child("status").value.toString()
                            if (productStatus == "wait") {
                                productSnapshot.ref.updateChildren(mapOf("status" to "complete"))
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle error if needed
                    }
                })
                Toast.makeText(this, "Mua hàng thành công!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@PurchaseActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi lưu thông tin thanh toán!", Toast.LENGTH_SHORT).show()
            }
    }

}