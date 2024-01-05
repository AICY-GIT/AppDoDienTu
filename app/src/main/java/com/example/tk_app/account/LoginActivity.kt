package com.example.tk_app.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.tk_app.MainActivity
import com.example.tk_app.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btLogin: Button;private lateinit var btRegister: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference;private lateinit var databaseReference1: DatabaseReference
    private lateinit var edtPass:TextInputEditText;private lateinit var edtEmail:TextInputEditText
    private lateinit var tilPass:TextInputLayout;private lateinit var tilEmail:TextInputLayout
    private var isValidEmail: Boolean = false;  private var isValidPassword: Boolean = false
    private lateinit var pass:String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        setContentView(R.layout.activity_login)
        Maping()
        edtEmail.addTextChangedListener(emailTextWatcher)
        edtPass.addTextChangedListener(passwordTextWatcher)


        databaseReference = FirebaseDatabase.getInstance().getReference("Account/User")
        databaseReference1 = FirebaseDatabase.getInstance().getReference("Account/Admin")

        btRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btLogin.setOnClickListener {
            if(isValidEmail&&isValidPassword){

                val email = edtEmail.text.toString()
                val password = edtPass.text.toString()
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
    fun Maping(){
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btLogin = findViewById(R.id.btLogin)
        btRegister = findViewById(R.id.btRegister)
        edtPass= findViewById(R.id.etPassword)
        edtEmail= findViewById(R.id.etEmail)
        tilPass = findViewById(R.id.til_Password_log)
        tilEmail = findViewById(R.id.til_Email_log);
    }
    private val emailTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not needed for this example
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Check email and update error message accordingly
            val email = s.toString()

            if (email.isEmpty()) {
                isValidEmail = false
                tilEmail.error = "Email cannot be empty"
            } else if (email.length < 5) {
                isValidEmail = false
                tilEmail.error = "Email must be at least 5 characters"
            } else if (!EmailValidator.isValidEmail(email)) {
                isValidEmail = false
                tilEmail.error = "Invalid email"
            } else {
                isValidEmail = true
                tilEmail.error = null
            }
        }

        override fun afterTextChanged(s: Editable?) {
            // Not needed for this example
        }
    }
    private val passwordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not needed for this example
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Check password and update error message accordingly
            val password = s.toString()

            if (password.isEmpty()) {
                isValidPassword = false
                tilPass.error = "Password cannot be empty"
            } else if (password.length < 6) {
                isValidPassword = false
                tilPass.error = "Password must be at least 6 characters"
            } else {
                isValidPassword = true
                pass = password
                tilPass.error = null
            }
        }

        override fun afterTextChanged(s: Editable?) {
            // Not needed for this example
        }
    }

    class EmailValidator {
        companion object {
            private const val EMAIL_REGEX =
                "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"

            private val pattern: Pattern = Pattern.compile(EMAIL_REGEX)

            fun isValidEmail(email: CharSequence?): Boolean {
                return email != null && pattern.matcher(email).matches()
            }
        }
    }
}