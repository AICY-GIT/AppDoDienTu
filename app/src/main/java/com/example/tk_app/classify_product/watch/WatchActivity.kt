package com.example.tk_app.classify_product.watch

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

class WatchActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private val uid4 = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private lateinit var click_Classify4: Spinner
    private lateinit var tv_Name_Product4: EditText
    private lateinit var tv_Price4: EditText
    private lateinit var tv_Details4: EditText
    private lateinit var tv_Origin4: EditText
    private lateinit var tv_Material4: EditText
    private lateinit var tv_Quantity4: EditText
    private lateinit var chkAuthentic4: CheckBox
    private lateinit var btn_Click_Save4: Button
    private lateinit var tv_Showimages4: ImageView
    private lateinit var btn_Click_Images4: Button

    // Khai báo cho việc chọn ảnh
    private val requestPermissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openImageChooser4()
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
                tv_Showimages4.setImageURI(selectedImageUri)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_electronic_device)
        databaseReference = FirebaseDatabase.getInstance().reference.child("Product").child("Classify").child("Electronic_Device")

        click_Classify4 = findViewById(R.id.click_classify4)
        tv_Name_Product4 = findViewById(R.id.tv_name_product4)
        tv_Price4 = findViewById(R.id.tv_price4)
        tv_Details4 = findViewById(R.id.tv_details4)
        tv_Origin4 = findViewById(R.id.tv_origin4)
        tv_Material4 = findViewById(R.id.tv_material4)
        tv_Quantity4 = findViewById(R.id.tv_quantity4)
        chkAuthentic4 = findViewById(R.id.chkAuthentic4)
        btn_Click_Save4 = findViewById(R.id.btn_click_save4)
        tv_Showimages4 = findViewById(R.id.tv_showimages4)
        btn_Click_Images4 = findViewById(R.id.btn_click_images4)

        val productTypes4 = resources.getStringArray(R.array.product_types_watch)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, productTypes4)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        click_Classify4.adapter = adapter


        // Xử lý sự kiện khi nút "Lưu" được click
        btn_Click_Save4.setOnClickListener {
            saveProduct()
        }
        btn_Click_Images4.setOnClickListener {
            checkPermissionAndOpenImageChooser()
        }
    }

    private fun saveProduct() {
        val type = click_Classify4.selectedItem.toString()
        val name = tv_Name_Product4.text.toString()
        var price = tv_Price4.text.toString()
        val details = tv_Details4.text.toString()
        val origin = tv_Origin4.text.toString()
        val material = tv_Material4.text.toString()
        val quantity = tv_Quantity4.text.toString()
        val authentic = chkAuthentic4.isChecked
        price = formatPrice(price.toDouble())

        val productelectronicId = databaseReference.push().key

        if (productelectronicId != null) {
            val productReference = databaseReference.child(productelectronicId)
            productReference.child("productelectronicId").setValue(productelectronicId)
            productReference.child("type").setValue(type)
            productReference.child("name").setValue(name)
            productReference.child("price").setValue(price)
            productReference.child("details").setValue(details)
            productReference.child("origin").setValue(origin)
            productReference.child("material").setValue(material)
            productReference.child("quantity").setValue(quantity)
            productReference.child("authentic").setValue(authentic)
            val imgSelectedDrawable = tv_Showimages4.drawable
            if (imgSelectedDrawable != null && imgSelectedDrawable is BitmapDrawable) {
                val bitmap = imgSelectedDrawable.bitmap
                val selectedImageUri = saveImageToStorage(bitmap, productelectronicId)
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
        val storageReference = FirebaseStorage.getInstance().reference.child("admin_add_product/electronic_device/$uid4/$productId.jpg")
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openImageChooser4()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun openImageChooser4() {
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