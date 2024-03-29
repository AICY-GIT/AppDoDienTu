package com.example.tk_app.classify_product.accessory

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
import com.example.tk_app.classify_product.phones.ProductPhone
import com.example.tk_app.review.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailAccessoryActivity : AppCompatActivity() {
    private lateinit var tv_Type_Product: TextView
    private lateinit var tv_Name_Product: TextView
    private lateinit var tv_Price_Product: TextView
    private lateinit var tv_Details_Product: TextView
    private lateinit var tv_Origin_Product: TextView
    private lateinit var tv_Material_Product: TextView
    private lateinit var tv_Quantity_Product: TextView
    private lateinit var ig_Images_Product: ImageView

    private lateinit var tv_Rate_Product: RatingBar
    private lateinit var tv_Rate_Product_text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detai_products)

        tv_Type_Product = findViewById(R.id.tv_type_product)
        tv_Name_Product = findViewById(R.id.tv_name_product)
        tv_Price_Product = findViewById(R.id.tv_price_product)
        ig_Images_Product = findViewById(R.id.ig_images_product)
        tv_Origin_Product = findViewById(R.id.tv_origin_product)
        tv_Material_Product = findViewById(R.id.tv_material_product)
        tv_Quantity_Product = findViewById(R.id.tv_quantity_product)
        tv_Details_Product = findViewById(R.id.tv_details_product)

        //thêm rate
        tv_Rate_Product = findViewById(R.id.tv_rate_product)
        tv_Rate_Product_text = findViewById(R.id.tv_rate_product_text)

        // Lấy productmenId từ Intent
        val productWomenId = intent.getStringExtra("productWomenId") ?: ""

        // Tải thông tin sản phẩm từ Firebase bằng productmenId
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("Product").child("Classify")
                .child("Accessory").child(productWomenId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val product = snapshot.getValue(ProductAccessory::class.java)
                    if (product != null) {
                        // Hiển thị thông tin sản phẩm trong các TextView và ImageView
                        tv_Type_Product.text = "${product?.type}"
                        tv_Name_Product.text = "${product?.name}"
                        tv_Price_Product.text = "${product?.price} VND"
                        tv_Details_Product.text = "${product?.details}"
                        tv_Origin_Product.text = "${product?.origin}"
                        tv_Material_Product.text = "${product?.material}"
                        tv_Quantity_Product.text = "${product?.quantity}"


                        if (product?.imageUrl != null) {
                            Glide.with(this@DetailAccessoryActivity)
                                .load(product.imageUrl)
                                .into(ig_Images_Product)
                        }

                        //thêm rate
                        val reviewsReference =
                            FirebaseDatabase.getInstance().reference.child("Reviews")
                        val productIdToCheck = product?.productWomenId

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
                                            0.00 // Hoặc giá trị mặc định khác tùy thuộc vào yêu cầu của bạn
                                        }

                                        // Lưu giá trị trung bình rate vào product2
                                        product.rate = averageRating
                                        tv_Rate_Product.setRating(averageRating.toFloat())
                                        tv_Rate_Product_text.text = "%.2f".format(averageRating)
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        // Xử lý lỗi nếu cần
                                    }
                                })
                        } else {
                            // Xử lý khi productWomenId không tồn tại (ví dụ: hiển thị giá trị mặc định)
                            tv_Rate_Product.setRating(0f)
                            tv_Rate_Product_text.text = "N/A"
                        }

                        //thêm rate
                        val btnReview = findViewById<Button>(R.id.btn_review)

                        btnReview.setOnClickListener {
                            val userUID = FirebaseAuth.getInstance().currentUser?.uid
                            if (userUID != null) {
                                val orderDetailsReference =
                                    FirebaseDatabase.getInstance().reference.child("OrderDetails")
                                        .child(userUID)

                                val orderDetailQuery = orderDetailsReference.orderByChild("productId").equalTo(productIdToCheck)

                                orderDetailQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // Nếu sản phẩm tồn tại trong giỏ hàng của người dùng, cho phép đánh giá
                                            val dialogBuilder = AlertDialog.Builder(this@DetailAccessoryActivity)
                                            dialogBuilder.setTitle("Đánh giá \"${product.name}\"")

                                            val dialogView = layoutInflater.inflate(R.layout.rating_dialog, null)
                                            val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)

                                            dialogBuilder.setView(dialogView)

                                            dialogBuilder.setPositiveButton("Lưu") { dialog, _ ->
                                                val userRating = ratingBar.rating.toDouble()
                                                val review = Review(
                                                    productId = product.productWomenId,
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
                                            // Nếu sản phẩm không tồn tại trong giỏ hàng của người dùng, hiển thị thông báo lỗi
                                            Toast.makeText(
                                                this@DetailAccessoryActivity,
                                                "Bạn cần mua sản phẩm để đánh giá.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        // Xử lý lỗi nếu cần thiết
                                    }
                                })
                            }
                        }


                        val btnShowDetails = findViewById<Button>(R.id.btn_show_details)
                        // Set an onClickListener for the button
                        btnShowDetails.setOnClickListener {
                            val dialogBuilder =
                                AlertDialog.Builder(this@DetailAccessoryActivity)
                            dialogBuilder.setTitle("Bạn muốn mua sản phẩm này?")

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
                            if (product.imageUrl != null) {
                                Glide.with(this@DetailAccessoryActivity)
                                    .load(product.imageUrl)
                                    .into(dialogImageView)
                            }

                            // Set the product name in the dialog
                            tv_Quantity_Show_Add_Men.text = product.quantity
                            dialogProductName.text = product.name
                            dialogProductPrice.text = "${product.price} VND"
                            editQuantity.setText("1")
                            btnDecrease.setOnClickListener {
                                val currentQuantity = editQuantity.text.toString().toInt()
                                if (currentQuantity > 1) {
                                    editQuantity.setText("Số lượng còn lại: "+(currentQuantity - 1).toString())
                                }
                            }

                            // Handle quantity increase button
                            btnIncrease.setOnClickListener {
                                val currentQuantity = editQuantity.text.toString().toInt()
                                editQuantity.setText((currentQuantity + 1).toString())
                            }
                            val btnSaveToDatabase =
                                dialogView.findViewById<Button>(R.id.btn_save_to_database)

                            btnSaveToDatabase.setOnClickListener {
                                // Lấy ID người dùng từ Firebase Authentication
                                val userUID = FirebaseAuth.getInstance().currentUser?.uid

                                if (userUID != null) {
                                    // Tạo một đối tượng Firebase Realtime Database
                                    val databaseReference =
                                        FirebaseDatabase.getInstance().reference.child("Cart")
                                            .child("Cart_Fashion").child(userUID)

                                    // Lấy các thuộc tính bạn muốn lưu
                                    val productName = product.name
                                    val productPrice = product.price
                                    val quantity = editQuantity.text.toString()
                                    val imageUrl = product.imageUrl

                                    val productData = mapOf(
                                        "userUID" to userUID,
                                        "name" to productName,
                                        "price" to productPrice,
                                        "quantity" to quantity,
                                        //sửa lại Id để lưu vào order
                                        "productId" to productWomenId,
                                        "status" to "confirmation pending",
                                        "imageUrl" to imageUrl
                                    )
                                    val query = databaseReference.orderByChild("productWomenId")
                                        .equalTo(productWomenId)

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
                                        this@DetailAccessoryActivity,
                                        CartActivity::class.java
                                    )
                                    startActivity(cartIntent)
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

