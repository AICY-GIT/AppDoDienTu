package com.example.tk_app.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.tk_app.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditAccountActivity : AppCompatActivity() {
    private var name: String? = null
    private var email: String? = null
    private var phone: String? = null
    private var userID: String? = null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var tilName: TextInputLayout
    private lateinit var tilPhone: TextInputLayout
    private lateinit var edName: TextInputEditText
    private lateinit var edPhone: TextInputEditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)
        val intent: Intent = intent
        userID = intent.getStringExtra("userId")
        name = intent.getStringExtra("name")
        email = intent.getStringExtra("email")
        phone = intent.getStringExtra("phone")

        tilName = findViewById(R.id.tilName)
        tilPhone = findViewById(R.id.tilPhone)
        edName = findViewById(R.id.edName)
        edPhone = findViewById(R.id.edPhone)
        btnSave = findViewById(R.id.btnSave)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Account/User").child(userID!!)

        edName.setText(name)
        edPhone.setText(phone)

        btnSave.setOnClickListener {
            if (edName.text.toString().isEmpty() || edPhone.text.toString().isEmpty()) {
                Toast.makeText(this@EditAccountActivity, "Không được để trống", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nameValue = edName.text.toString()
            val phoneValue = edPhone.text.toString()

            // Cập nhật dữ liệu vào Realtime Database
            databaseReference.child("Name").setValue(nameValue)
            databaseReference.child("Phone").setValue(phoneValue)

            Toast.makeText(this@EditAccountActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
