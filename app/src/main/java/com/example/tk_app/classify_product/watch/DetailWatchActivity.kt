package com.example.tk_app.classify_product.watch

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.tk_app.R
import com.example.tk_app.account.LoginActivity
import com.example.tk_app.classify_product.CartActivity
import com.example.tk_app.review.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailWatchActivity : AppCompatActivity() {
    private lateinit var tv_Type_Product_Electronic: TextView
    private lateinit var tv_Name_Product_Electronic: TextView
    private lateinit var tv_Price_Product_Electronic: TextView
    private lateinit var tv_Details_Product_Electronic: TextView
    private lateinit var tv_Origin_Product_Electronic: TextView
    private lateinit var tv_Material_Product_Electronic: TextView
    private lateinit var tv_Quantity_Product_Electronic: TextView
    private lateinit var ig_Images_Product_Electronic: ImageView

    private lateinit var tv_Rate_Product: TextView

    private val uid4 = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_watch)
        tv_Type_Product_Electronic = findViewById(R.id.tv_type_product_electronic)
        tv_Name_Product_Electronic = findViewById(R.id.tv_name_product_electronic)
        tv_Price_Product_Electronic = findViewById(R.id.tv_price_product_electronic)
        ig_Images_Product_Electronic = findViewById(R.id.ig_images_product_electronic)
        tv_Origin_Product_Electronic = findViewById(R.id.tv_origin_product_electronic)
        tv_Material_Product_Electronic = findViewById(R.id.tv_material_product_electronic)
        tv_Quantity_Product_Electronic = findViewById(R.id.tv_quantity_product_electronic)
        tv_Details_Product_Electronic = findViewById(R.id.tv_details_product_electronic)

        tv_Rate_Product = findViewById(R.id.tv_rate_product_electronic)

        // Lấy productmenId từ Intent
        val productelectronicId = intent.getStringExtra("productelectronicId") ?: ""

        val uid = "HJqF0S5j3cM7VImvgyTjxhE4D6e2"
        // Tải thông tin sản phẩm từ Firebase bằng productmenId
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("Product").child("Classify")
                .child("Watch").child(productelectronicId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val product4 = snapshot.getValue(ProductWatch::class.java)
                    if (product4 != null) {
                        // Hiển thị thông tin sản phẩm trong các TextView và ImageView
                        tv_Type_Product_Electronic.text = "Type: ${product4?.type}"
                        tv_Name_Product_Electronic.text = "Product Name: ${product4?.name}"
                        tv_Price_Product_Electronic.text = "Price: ${product4?.price}"
                        tv_Details_Product_Electronic.text = "Details: ${product4?.details}"
                        tv_Origin_Product_Electronic.text = "Origin: ${product4?.origin}"
                        tv_Material_Product_Electronic.text = "Material: ${product4?.material}"
                        tv_Quantity_Product_Electronic.text = "Quantity: ${product4?.quantity}"


                        if (product4?.imageUrl != null) {
                            Glide.with(this@DetailWatchActivity)
                                .load(product4.imageUrl)
                                .into(ig_Images_Product_Electronic)
                        }

                        //thêm rate
                        val reviewsReference =
                            FirebaseDatabase.getInstance().reference.child("Reviews")
                        val productIdToCheck = product4?.productelectronicId

                        // Kiểm tra xem productId có tồn tại
                        if (productIdToCheck != null) {
                            reviewsReference.orderByChild("productId")
                                .equalTo(productIdToCheck)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        var totalRating = 0.0
                                        var numberOfReviews = 0

                                        // Duyệt qua tất cả các đánh giá
                                        for (reviewSnapshot in dataSnapshot.children) {
                                            val review = reviewSnapshot.getValue(Review::class.java)
                                            review?.let {
                                                // Lấy giá trị rate từ đánh giá và cộng dồn
                                                val rate = it.rate
                                                if (rate != null) {
                                                    totalRating += rate
                                                    numberOfReviews++
                                                }
                                            }
                                        }

                                        // Tính trung bình rate
                                        val averageRating = if (numberOfReviews > 0) {
                                            totalRating / numberOfReviews
                                        } else {
                                            1.0 // Hoặc giá trị mặc định khác tùy thuộc vào yêu cầu của bạn
                                        }

                                        // Lưu giá trị trung bình rate vào product2
                                        product4.rate = averageRating
                                        tv_Rate_Product.text = "Rate: %.2f".format(averageRating)
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        // Xử lý lỗi nếu cần
                                    }
                                })
                        } else {
                            // Xử lý khi productWomenId không tồn tại (ví dụ: hiển thị giá trị mặc định)
                            tv_Rate_Product.text = "Rate: N/A"
                        }

                        //thêm rate
                        val btnReview4 = findViewById<Button>(R.id.btn_review4)

                        btnReview4.setOnClickListener {
                            val userUID = FirebaseAuth.getInstance().currentUser?.uid
                            if (userUID != null) {
                                val cartFashionReference =
                                    FirebaseDatabase.getInstance().reference.child("Cart")
                                        .child("Cart_Fashion")
                                        .child(userUID)

                                // Kiểm tra sự tồn tại của productId trong Cart_Fashion của người dùng
                                val productIdToCheck = product4.productelectronicId
                                cartFashionReference.orderByChild("productelectronicId")
                                    .equalTo(productIdToCheck)
                                    .addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                // Nếu productId tồn tại, cho phép người dùng đánh giá
                                                val dialogBuilder =
                                                    AlertDialog.Builder(this@DetailWatchActivity)
                                                dialogBuilder.setTitle("Đánh giá \"${product4.name}\"")

                                                val dialogView = layoutInflater.inflate(
                                                    R.layout.rating_dialog,
                                                    null
                                                )
                                                val ratingBar =
                                                    dialogView.findViewById<RatingBar>(R.id.ratingBar)

                                                dialogBuilder.setView(dialogView)

                                                dialogBuilder.setPositiveButton("Lưu") { dialog, _ ->
                                                    val userRating = ratingBar.rating.toDouble()
                                                    val review = Review(
                                                        productId = product4.productelectronicId,
                                                        userId = userUID,
                                                        rate = userRating
                                                    )

                                                    // Thêm đánh giá vào Firebase Realtime Database
                                                    saveReviewToFirebase(review)

                                                    // Cập nhật thông tin hiển thị trên màn hình
                                                    recreate()

                                                    dialog.dismiss()
                                                }
                                                dialogBuilder.setNegativeButton("Hủy") { dialog, _ ->
                                                    dialog.dismiss()
                                                }

                                                val dialog = dialogBuilder.create()
                                                dialog.show()

                                            } else {
                                                // Nếu productId không tồn tại, thông báo lỗi
                                                Toast.makeText(
                                                    this@DetailWatchActivity,
                                                    "Bạn cần mua sản phẩm để đánh giá.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            // Xử lý lỗi nếu cần
                                        }
                                    })


                            }
                        }
                        val btnShowDetails4 = findViewById<Button>(R.id.btn_show_details4)
                        // Set an onClickListener for the button
                        btnShowDetails4.setOnClickListener {
                            val dialogBuilder = AlertDialog.Builder(this@DetailWatchActivity)
                            dialogBuilder.setTitle("Product Details")

                            val dialogView =
                                layoutInflater.inflate(R.layout.dialog_product_cart, null)

                            val dialogImageView =
                                dialogView.findViewById<ImageView>(R.id.dialogImageView)
                            val dialogProductName =
                                dialogView.findViewById<TextView>(R.id.dialogProductName)
                            val dialogProductPrice =
                                dialogView.findViewById<TextView>(R.id.dialogProductPrice)
                            val btnDecrease = dialogView.findViewById<Button>(R.id.btnDecrease)
                            val editQuantity = dialogView.findViewById<EditText>(R.id.editQuantity)
                            val btnIncrease = dialogView.findViewById<Button>(R.id.btnIncrease)
                            val tv_Quantity_Show_Add_Men =
                                dialogView.findViewById<TextView>(R.id.tv_quantity_show_add_men)


                            // Load the image into the dialog
                            if (product4.imageUrl != null) {
                                Glide.with(this@DetailWatchActivity)
                                    .load(product4.imageUrl)
                                    .into(dialogImageView)
                            }

                            // Set the product name in the dialog
                            tv_Quantity_Show_Add_Men.text = product4.quantity
                            dialogProductName.text = product4.name
                            dialogProductPrice.text = "Price: ${product4.price}"
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
                            val btnSaveToDatabase =
                                dialogView.findViewById<Button>(R.id.btn_save_to_database)
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            if (currentUser == null) {
                                val intent =
                                    Intent(this@DetailWatchActivity, LoginActivity::class.java)
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
                                        val productName = product4.name
                                        val productPrice = product4.price
                                        val quantity = editQuantity.text.toString()
                                        val imageUrl = product4.imageUrl

                                        val productData = mapOf(
                                            "userUID" to userUID,
                                            "name" to productName,
                                            "price" to productPrice,
                                            "quantity" to quantity,
                                            "productelectronicId" to productelectronicId,
                                            "status" to "confirmation pending",
                                            "imageUrl" to imageUrl
                                        )
                                        val query =
                                            databaseReference.orderByChild("productelectronicId")
                                                .equalTo(productelectronicId)

                                        query.addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (childSnapshot in dataSnapshot.children) {
                                                        // Cập nhật thông tin sản phẩm trong giỏ hàng và thêm trạng thái "wait"
                                                        childSnapshot.ref.updateChildren(
                                                            productData + mapOf(
                                                                "status" to "wait"
                                                            )
                                                        )
                                                    }
                                                } else {
                                                    // Sản phẩm không tồn tại trong giỏ hàng, tạo mới với trạng thái "wait"
                                                    databaseReference.push()
                                                        .setValue(productData + mapOf("status" to "wait"))
                                                }
                                            }

                                            override fun onCancelled(databaseError: DatabaseError) {
                                                // Xử lý lỗi nếu cần
                                            }
                                        })


                                        val cartIntent = Intent(
                                            this@DetailWatchActivity,
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

    private fun saveReviewToFirebase(review: Review) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("Reviews")

        // Tạo một key mới cho đánh giá
        val reviewKey = databaseReference.push().key

        // Đặt giá trị của đánh giá tại key mới tạo
        reviewKey?.let {
            databaseReference.child(it).setValue(review)
        }
    }
}
