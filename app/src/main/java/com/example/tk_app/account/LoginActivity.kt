package com.example.tk_app.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.tk_app.MainActivity
import com.example.tk_app.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btLogin: Button
    private lateinit var btRegister: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReference1: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btLogin = findViewById(R.id.btLogin)
        btRegister = findViewById(R.id.btRegister)
        databaseReference = FirebaseDatabase.getInstance().getReference("Account/User")
        databaseReference1 = FirebaseDatabase.getInstance().getReference("Account/Admin")

        btRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "Email không được bỏ trống!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "Mật khẩu không được bỏ trống!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (!task.isSuccessful) {
                        if (password.length < 6) {
                            etPassword.error = getString(R.string.minimum_password)
                        } else {
                            Toast.makeText(this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // Đăng nhập thành công
                        val currentUserUID = auth.currentUser?.uid
                        val userRef = databaseReference.child(currentUserUID!!)
                        val adminRef = databaseReference1.child(currentUserUID!!)
                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val userType = dataSnapshot.child("userType").value.toString()

                                    when (userType) {
                                        "user" -> {
                                            Toast.makeText(this@LoginActivity, "Mua sắm thỏa thích cùng Tiki", Toast.LENGTH_LONG).show()
                                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                            finish()
                                        }
                                        else -> {
                                            Toast.makeText(this@LoginActivity, "Loại người dùng không hợp lệ", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } else {
                                    // Nếu không tìm thấy thông tin người dùng trong phần user, kiểm tra phần admin
                                    adminRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                val userType = dataSnapshot.child("userType").value.toString()

                                                when (userType) {
                                                    "admin" -> {
                                                        Toast.makeText(this@LoginActivity, "Tiki Admin", Toast.LENGTH_LONG).show()
                                                        startActivity(Intent(this@LoginActivity, AdminActivity::class.java))
                                                        finish()
                                                    }
                                                    else -> {
                                                        Toast.makeText(this@LoginActivity, "Loại người dùng không hợp lệ", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(this@LoginActivity, "Không tìm thấy thông tin người dùng", Toast.LENGTH_LONG).show()
                                            }
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            Toast.makeText(this@LoginActivity, "Lỗi khi truy cập dữ liệu người dùng", Toast.LENGTH_LONG).show()
                                        }
                                    })
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Toast.makeText(this@LoginActivity, "Lỗi khi truy cập dữ liệu người dùng", Toast.LENGTH_LONG).show()
                            }
                        })
                    }
                })

        }
    }
}