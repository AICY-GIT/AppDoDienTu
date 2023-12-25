package com.example.tk_app.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.tk_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private lateinit var etRName: EditText
    private lateinit var etRPhone: EditText
    private lateinit var etREmail: EditText
    private lateinit var etRPassword: EditText
    private lateinit var etVPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        etRName = findViewById(R.id.etRName)
        etRPhone = findViewById(R.id.etRPhone)
        etREmail = findViewById(R.id.etREmail)
        etRPassword = findViewById(R.id.etRPassword)
        etVPassword = findViewById(R.id.etVPassword)
        btnRegister = findViewById(R.id.btnRegister)
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Account/User")

        btnRegister.setOnClickListener {
            val name = etRName.text.toString().trim()
            val phone = etRPhone.text.toString().trim()
            val email = etREmail.text.toString().trim()
            val password = etRPassword.text.toString().trim()
            val vpassword = etVPassword.text.toString().trim()
            val emailPattern = "[a-zA-Z0-9._-]+@gmail\\.com"
            val emailMatcher = Pattern.compile(emailPattern).matcher(email)
            if (!emailMatcher.matches()) {
                Toast.makeText(applicationContext, "Email không hợp lệ!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Kiểm tra số điện thoại có đúng 11 số không
            if (phone.length != 11) {
                Toast.makeText(applicationContext, "Số điện thoại phải có đúng 11 số!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(applicationContext, "Họ tên không được bỏ trống!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(applicationContext, "Số điện thoại không được bỏ trống!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "Email không được bỏ trống!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "Mật khẩu không được bỏ trống!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(applicationContext, R.string.minimum_password, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password != vpassword) {
                Toast.makeText(applicationContext, "Mật khẩu xác nhận không đúng!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userID = auth.currentUser?.uid
                    val currentUser = databaseReference.child(userID!!)

                    val user = hashMapOf(
                        "Name" to etRName.text.toString(),
                        "Email" to etREmail.text.toString(),
                        "Phone" to etRPhone.text.toString(),
                        "userType" to "user"
                    )

                    currentUser.setValue(user).addOnCompleteListener {
                        // Đăng ký người dùng thành công
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}