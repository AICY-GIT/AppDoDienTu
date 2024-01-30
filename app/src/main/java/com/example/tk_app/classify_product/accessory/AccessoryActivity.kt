package com.example.tk_app.classify_product.accessory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.tk_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class AccessoryActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private val uid2 = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private lateinit var click_Classify2: Spinner
    private lateinit var tv_Name_Product2: EditText
    private lateinit var tv_Price2: EditText
    private lateinit var tv_Details2: EditText
    private lateinit var tv_Origin2: EditText
    private lateinit var tv_Material2: EditText
    private lateinit var tv_Quantity2: EditText
    private lateinit var chkAuthentic2: CheckBox
    private lateinit var btn_Click_Save2: Button
    private lateinit var tv_Showimages2: ImageView
    private lateinit var btn_Click_Images2: Button

    // Khai báo cho việc chọn ảnh
    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                openImageChooser2()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val takePictureLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                tv_Showimages2.setImageURI(selectedImageUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_accessory)
        databaseReference =
            FirebaseDatabase.getInstance().reference.child("Product").child("Classify")
                .child("Accessory")

        click_Classify2 = findViewById(R.id.click_classify2)
        tv_Name_Product2 = findViewById(R.id.tv_name_product2)
        tv_Price2 = findViewById(R.id.tv_price2)
        tv_Details2 = findViewById(R.id.tv_details2)
        tv_Origin2 = findViewById(R.id.tv_origin2)
        tv_Material2 = findViewById(R.id.tv_material2)
        tv_Quantity2 = findViewById(R.id.tv_quantity2)
        chkAuthentic2 = findViewById(R.id.chkAuthentic2)
        btn_Click_Save2 = findViewById(R.id.btn_click_save2)
        tv_Showimages2 = findViewById(R.id.tv_showimages2)
        btn_Click_Images2 = findViewById(R.id.btn_click_images2)

        val productTypes2 = resources.getStringArray(R.array.product_types_accessory)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, productTypes2)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        click_Classify2.adapter = adapter


        // Xử lý sự kiện khi nút "Lưu" được click
        btn_Click_Save2.setOnClickListener {
            saveProduct()
        }
        btn_Click_Images2.setOnClickListener {
            checkPermissionAndOpenImageChooser()
        }
    }

    private fun saveProduct() {
        val type = click_Classify2.selectedItem.toString()
        val name = tv_Name_Product2.text.toString()
        var price = tv_Price2.text.toString()
        val details = tv_Details2.text.toString()
        val origin = tv_Origin2.text.toString()
        val material = tv_Material2.text.toString()
        val quantity = tv_Quantity2.text.toString()
        val authentic = chkAuthentic2.isChecked
        val rate = 0.00
        price = formatPrice(price.toDouble())

        val productWomenId = databaseReference.push().key

        if (productWomenId != null) {
            val productReference = databaseReference.child(productWomenId)
            productReference.child("productWomenId").setValue(productWomenId)
            productReference.child("type").setValue(type)
            productReference.child("name").setValue(name)
            productReference.child("price").setValue(price)
            productReference.child("details").setValue(details)
            productReference.child("origin").setValue(origin)
            productReference.child("material").setValue(material)
            productReference.child("quantity").setValue(quantity)
            productReference.child("authentic").setValue(authentic)
            //thêm rate
            productReference.child("rate").setValue(rate)

            val imgSelectedDrawable = tv_Showimages2.drawable
            if (imgSelectedDrawable != null && imgSelectedDrawable is BitmapDrawable) {
                val bitmap = imgSelectedDrawable.bitmap
                val selectedImageUri = saveImageToStorage(bitmap, productWomenId)
                productReference.child("imageUrl").setValue(selectedImageUri.toString())
            } else {
                // Handle case when there is no selected image
                productReference.child("imageUrl").setValue("")
            }
        }
        Toast.makeText(this, "Sản phẩm đã được lưu", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun saveImageToStorage(bitmap: Bitmap, productId: String) {
        val storageReference =
            FirebaseStorage.getInstance().reference.child("admin_add_product/product_women_fashion/$uid2/$productId.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        storageReference.putBytes(data)
            .addOnSuccessListener { taskSnapshot ->
                // Image upload success
                storageReference.downloadUrl
                    .addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        updateProductImageUrl(productId, imageUrl)
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseStorage", "Failed to get image URL: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                // Handle failure to upload the image
                Log.e("FirebaseStorage", "Failed to upload image: ${e.message}")
            }
    }


    private fun updateProductImageUrl(productId: String, imageUrl: String) {
        val productReference = databaseReference.child(productId)
        productReference.child("imageUrl").setValue(imageUrl)
    }

    private fun checkPermissionAndOpenImageChooser() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openImageChooser2()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun openImageChooser2() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        takePictureLauncher.launch(intent)
    }

    private fun isNumeric(str: String): Boolean {
        return str.matches("-?\\d+(\\.\\d+)?".toRegex())
    }

    private fun formatPrice(price: Double): String {
        val priceString = String.format("%.0f", price) // Chuyển đổi giá thành chuỗi số nguyên
        val reversedPrice = priceString.reversed() // Đảo ngược chuỗi
        val stringBuilder = StringBuilder()
        for (i in 0 until reversedPrice.length) {
            stringBuilder.append(reversedPrice[i])
            if ((i + 1) % 3 == 0 && i != reversedPrice.length - 1) {
                stringBuilder.append('.')
            }
        }
        return stringBuilder.reverse().toString()
    }
}