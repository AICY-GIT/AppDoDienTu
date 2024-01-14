package com.example.tk_app.classify_product.earphones_accessories

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.tk_app.R
import com.example.tk_app.account.LoginActivity
import com.example.tk_app.classify_product.CartActivity
import com.example.tk_app.classify_product.accessory.ProductAccessory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailEarPhoneActivity : AppCompatActivity() {
    private lateinit var tv_Type_Product_Phone: TextView
    private lateinit var tv_Name_Product_Phone: TextView
    private lateinit var tv_Price_Product_Phone: TextView
    private lateinit var tv_Details_Product_Phone: TextView
    private lateinit var tv_Origin_Product_Phone: TextView
    private lateinit var tv_Material_Product_Phone: TextView
    private lateinit var tv_Quantity_Product_Phone: TextView
    private lateinit var ig_Images_Product_Phone: ImageView
    private val uid3 = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_phones_accessories)
        tv_Type_Product_Phone = findViewById(R.id.tv_type_product_phone)
        tv_Name_Product_Phone = findViewById(R.id.tv_name_product_phone)
        tv_Price_Product_Phone = findViewById(R.id.tv_price_product_phone)
        ig_Images_Product_Phone = findViewById(R.id.ig_images_product_phone)
        tv_Origin_Product_Phone = findViewById(R.id.tv_origin_product_phone)
        tv_Material_Product_Phone = findViewById(R.id.tv_material_product_phone)
        tv_Quantity_Product_Phone = findViewById(R.id.tv_quantity_product_phone)
        tv_Details_Product_Phone = findViewById(R.id.tv_details_product_phone)
        // Lấy productmenId từ Intent
        val productphoneId = intent.getStringExtra("productphoneId") ?: ""


        val uid = "HJqF0S5j3cM7VImvgyTjxhE4D6e2"
        // Tải thông tin sản phẩm từ Firebase bằng productmenId
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("Product").child("Classify")
                .child("Ear_Phones").child(productphoneId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val product3 = snapshot.getValue(ProductAccessory::class.java)
                    if (product3 != null) {
                        // Hiển thị thông tin sản phẩm trong các TextView và ImageView
                        tv_Type_Product_Phone.text = "Type: ${product3?.type}"
                        tv_Name_Product_Phone.text = "Product Name: ${product3?.name}"
                        tv_Price_Product_Phone.text = "Price: ${product3?.price}"
                        tv_Details_Product_Phone.text = "Details: ${product3?.details}"
                        tv_Origin_Product_Phone.text = "Origin: ${product3?.origin}"
                        tv_Material_Product_Phone.text = "Material: ${product3?.material}"
                        tv_Quantity_Product_Phone.text = "Quantity: ${product3?.quantity}"


                        if (product3?.imageUrl != null) {
                            Glide.with(this@DetailEarPhoneActivity)
                                .load(product3.imageUrl)
                                .into(ig_Images_Product_Phone)
                        }
                        val btnShowDetails3 = findViewById<Button>(R.id.btn_show_details3)
                        // Set an onClickListener for the button
                        btnShowDetails3.setOnClickListener {
                            val dialogBuilder = AlertDialog.Builder(this@DetailEarPhoneActivity)
                            dialogBuilder.setTitle("Product Details")

                            val dialogView = layoutInflater.inflate(R.layout.dialog_product_cart, null)

                            val dialogImageView = dialogView.findViewById<ImageView>(R.id.dialogImageView)
                            val dialogProductName = dialogView.findViewById<TextView>(R.id.dialogProductName)
                            val dialogProductPrice = dialogView.findViewById<TextView>(R.id.dialogProductPrice)
                            val btnDecrease = dialogView.findViewById<Button>(R.id.btnDecrease)
                            val editQuantity = dialogView.findViewById<EditText>(R.id.editQuantity)
                            val btnIncrease = dialogView.findViewById<Button>(R.id.btnIncrease)
                            val tv_Quantity_Show_Add_Men = dialogView.findViewById<TextView>(R.id.tv_quantity_show_add_men)




                            // Load the image into the dialog
                            if (product3.imageUrl != null) {
                                Glide.with(this@DetailEarPhoneActivity)
                                    .load(product3.imageUrl)
                                    .into(dialogImageView)
                            }

                            // Set the product name in the dialog
                            tv_Quantity_Show_Add_Men.text = product3.quantity
                            dialogProductName.text = product3.name
                            dialogProductPrice.text = "Price: ${product3.price}"
                            editQuantity.setText("1")
                            btnDecrease.setOnClickListener {
                                val currentQuantity = editQuantity.text.toString().toInt()
                                if (currentQuantity > 1) {
                                    editQuantity.setText((currentQuantity - 1).toString())
                                }
                            }

                            // Handle quantity increase button
                            btnIncrease.setOnClickListener {
                                val currentQuantity = editQuantity.text.toString().toInt()
                                editQuantity.setText((currentQuantity + 1).toString())
                            }
                            val btnSaveToDatabase = dialogView.findViewById<Button>(R.id.btn_save_to_database)
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            if (currentUser == null) {
                                val intent = Intent(this@DetailEarPhoneActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish() // Đóng màn hình giỏ hàng
                            } else {
                                btnSaveToDatabase.setOnClickListener {
                                    // Lấy ID người dùng từ Firebase Authentication
                                    val userUID = FirebaseAuth.getInstance().currentUser?.uid

                                    if (userUID != null) {
                                        // Tạo một đối tượng Firebase Realtime Database
                                        val databaseReference =
                                            FirebaseDatabase.getInstance().reference.child("Cart")
                                                .child("Cart_Fashion").child(userUID)

                                        // Lấy các thuộc tính bạn muốn lưu
                                        val productName = product3.name
                                        val productPrice = product3.price
                                        val quantity = editQuantity.text.toString()
                                        val imageUrl = product3.imageUrl

                                        val productData = mapOf(
                                            "userUID" to userUID,
                                            "name" to productName,
                                            "price" to productPrice,
                                            "quantity" to quantity,
                                            "productphoneId" to productphoneId,
                                            "status" to "confirmation pending",
                                            "imageUrl" to imageUrl
                                        )
                                        val query = databaseReference.orderByChild("productphoneId")
                                            .equalTo(productphoneId)

                                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (childSnapshot in dataSnapshot.children) {
                                                        // Cập nhật thông tin sản phẩm trong giỏ hàng và thêm trạng thái "wait"
                                                        childSnapshot.ref.updateChildren(productData + mapOf("status" to "wait"))
                                                    }
                                                } else {
                                                    // Sản phẩm không tồn tại trong giỏ hàng, tạo mới với trạng thái "wait"
                                                    databaseReference.push().setValue(productData + mapOf("status" to "wait"))
                                                }
                                            }

                                            override fun onCancelled(databaseError: DatabaseError) {
                                                // Xử lý lỗi nếu cần
                                            }
                                        })


                                        val cartIntent = Intent(
                                            this@DetailEarPhoneActivity,
                                            CartActivity::class.java
                                        )
                                        startActivity(cartIntent)
                                    }
                                }
                            }

                            dialogBuilder.setView(dialogView)
                            dialogBuilder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }

                            val dialog = dialogBuilder.create()
                            val lp = dialog.window?.attributes
                            lp?.gravity = Gravity.BOTTOM
                            dialog.window?.attributes = lp
                            dialog.show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu cần
            }
        })
    }
}
