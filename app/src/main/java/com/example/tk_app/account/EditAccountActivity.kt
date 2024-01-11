package com.example.tk_app.account

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tk_app.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class EditAccountActivity : AppCompatActivity() {
    private var name: String? = null
    private var email: String? = null
    private var phone: String? = null
    private var userID: String? = null
    private var imageUrl: String?=null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var tilName: TextInputLayout
    private lateinit var tilPhone: TextInputLayout
    private lateinit var edName: TextInputEditText
    private lateinit var edPhone: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnOut: Button

    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var imagePro: ImageView
    private lateinit var btnUpImg:Button

    //lấy url ảnh?
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                imagePro.setImageURI(selectedImageUri)
            }
        }
    }
    //lưu ảnh trên storage
    private fun saveImageToStorage(bitmap: Bitmap, productId: String) {
        val storageReference = FirebaseStorage.getInstance().reference.child("Profile/$productId.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        storageReference.putBytes(data)
            .addOnSuccessListener { taskSnapshot ->
                storageReference.downloadUrl
                    .addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        databaseReference.child("Account/User/$userID/Image").setValue(imageUrl)
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseStorage", "Failed to get image URL: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseStorage", "Failed to upload image: ${e.message}")
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account_2)

        val intent: Intent = intent
        userID = intent.getStringExtra("userId")
        name = intent.getStringExtra("name")
        email = intent.getStringExtra("email")
        phone = intent.getStringExtra("phone")
        imageUrl=intent.getStringExtra("imageUrl")

        tilName = findViewById(R.id.tilName)
        tilPhone = findViewById(R.id.tilPhone)
        edName = findViewById(R.id.edName)
        edPhone = findViewById(R.id.edPhone)
        btnSave = findViewById(R.id.btnSave)
        btnOut = findViewById(R.id.btnOut)
        imagePro = findViewById(R.id.imgchange)
        btnUpImg= this.findViewById(R.id.btnUploadImage)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        edName.setText(name)
        edPhone.setText(phone)
        imageUrl?.let { url ->
            Glide.with(this)
                .load(url)
                .into(imagePro)
        }

        btnSave.setOnClickListener {
            if (edName.text.toString().isEmpty() || edPhone.text.toString().isEmpty()) {
                Toast.makeText(this@EditAccountActivity, "Không được để trống", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nameValue = edName.text.toString()
            val phoneValue = edPhone.text.toString()

            databaseReference.child("Account/User/$userID/Name").setValue(nameValue)
            databaseReference.child("Account/User/$userID/Phone").setValue(phoneValue)

            //thay đổi trên realtime
            val imgSelectedDrawable = imagePro.drawable
            if (imgSelectedDrawable != null && imgSelectedDrawable is BitmapDrawable) {
                val bitmap = imgSelectedDrawable.bitmap
                saveImageToStorage(bitmap, userID ?: "")
            } else {
                databaseReference.child("Account/User/$userID/Image").setValue("null")
            }

            Toast.makeText(this@EditAccountActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnOut.setOnClickListener {
            onBackPressed()
        }

        imagePro.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            takePictureLauncher.launch(intent)
        }
        btnUpImg.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            takePictureLauncher.launch(intent)
        }

    }
}
