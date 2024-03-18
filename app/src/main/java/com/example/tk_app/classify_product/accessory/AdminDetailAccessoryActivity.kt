package com.example.tk_app.classify_product.accessory

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.tk_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class AdminDetailAccessoryActivity : AppCompatActivity() {
    private val uid2 = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private lateinit var databaseReference: DatabaseReference
    private lateinit var ed_Type_Product: EditText
    private lateinit var ed_Name_Product: EditText
    private lateinit var ed_Price_Product: EditText
    private lateinit var ed_Details_Product: EditText
    private lateinit var ed_Origin_Product: EditText
    private lateinit var ed_Material_Product: EditText
    private lateinit var ed_Quantity_Product: EditText
    private lateinit var ig_Images_Product_edit: ImageView
    private lateinit var productId: String

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openImageChooser()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                if (selectedImageUri != null) {
                    ig_Images_Product_edit.setImageURI(selectedImageUri)
                    // Lưu đường dẫn hình ảnh được chọn
                    saveImageToStorage(selectedImageUri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_detai_products)

        ed_Type_Product = findViewById(R.id.ed_type_product)
        ed_Name_Product = findViewById(R.id.ed_name_product)
        ed_Price_Product = findViewById(R.id.ed_price_product)
        ed_Details_Product = findViewById(R.id.ed_details_product)
        ed_Origin_Product = findViewById(R.id.ed_origin_product)
        ed_Material_Product = findViewById(R.id.ed_material_product)
        ed_Quantity_Product = findViewById(R.id.ed_quantity_product)
        ig_Images_Product_edit = findViewById(R.id.ig_images_product_edit)

        // Lấy productmenId từ Intent
        productId = intent.getStringExtra("productWomenId") ?: ""

        // Tải thông tin sản phẩm từ Firebase bằng productmenId
        databaseReference =
            FirebaseDatabase.getInstance().reference.child("Product").child("Classify")
                .child("Accessory").child(productId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val product = snapshot.getValue(ProductAccessory::class.java)
                    if (product != null) {
                        // Hiển thị thông tin sản phẩm trong các EditText và ImageView
                        ed_Type_Product.setText(product.type)
                        ed_Name_Product.setText(product.name)
                        ed_Price_Product.setText("${product.price}")
                        ed_Details_Product.setText(product.details)
                        ed_Origin_Product.setText(product.origin)
                        ed_Material_Product.setText(product.material)
                        ed_Quantity_Product.setText(product.quantity)

                        if (!isDestroyed) {
                            Glide.with(this@AdminDetailAccessoryActivity)
                                .load(product.imageUrl)
                                .into(ig_Images_Product_edit)
                        }

                        val btnDelete = findViewById<Button>(R.id.btn_delete)
                        btnDelete.setOnClickListener {
                            showDeleteConfirmationDialog()
                        }

                        val btnEdit = findViewById<Button>(R.id.btn_edit)
                        btnEdit.setOnClickListener {
                            updateProductData()
                        }

                        ig_Images_Product_edit.setOnClickListener {
                            checkPermissionAndOpenImageChooser()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    }

    private fun checkPermissionAndOpenImageChooser() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openImageChooser()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        takePictureLauncher.launch(intent)
    }

    private fun saveImageToStorage(imageUri: Uri) {
        val storageReference =
            FirebaseStorage.getInstance().reference.child("admin_add_product/product_women_fashion/$uid2/$productId.jpg")

        storageReference.putFile(imageUri)
            .addOnSuccessListener { _ ->
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    updateProductImageUrl(imageUrl)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseStorage", "Failed to upload image: ${e.message}")
            }
    }

    private fun updateProductImageUrl(imageUrl: String) {
        databaseReference.child("imageUrl").setValue(imageUrl)
    }

    private fun showDeleteConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Xác nhận xóa")
        alertDialogBuilder.setMessage("Bạn muốn xóa sản phẩm này?")

        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            deleteProduct()
        }

        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        alertDialogBuilder.create().show()
    }

    private fun deleteProduct() {
        databaseReference.removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@AdminDetailAccessoryActivity,
                        "Xóa sản phẩm thành công",
                        Toast.LENGTH_SHORT
                    ).show()
                    onBackPressed()
                } else {
                    Toast.makeText(
                        this@AdminDetailAccessoryActivity,
                        "Xóa sản phẩm thất bại",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun updateProductData() {
        val newType = ed_Type_Product.text.toString().trim()
        val newName = ed_Name_Product.text.toString().trim()
        val newPrice = ed_Price_Product.text.toString().trim()
        val newDetails = ed_Details_Product.text.toString().trim()
        val newOrigin = ed_Origin_Product.text.toString().trim()
        val newMaterial = ed_Material_Product.text.toString().trim()
        val newQuantity = ed_Quantity_Product.text.toString().trim()

        val updateData = mapOf(
            "type" to newType,
            "name" to newName,
            "price" to newPrice,
            "details" to newDetails,
            "origin" to newOrigin,
            "material" to newMaterial,
            "quantity" to newQuantity
        )

        databaseReference.updateChildren(updateData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@AdminDetailAccessoryActivity,
                        "Cập nhật hình ảnh thành công",
                        Toast.LENGTH_SHORT
                    ).show()
                    onBackPressed()
                } else {
                    Toast.makeText(
                        this@AdminDetailAccessoryActivity,
                        "Cập nhật hình ảnh thất bại",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
