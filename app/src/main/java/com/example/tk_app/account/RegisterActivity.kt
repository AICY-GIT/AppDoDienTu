package com.example.tk_app.account

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.tk_app.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private lateinit var btnSignUp:AppCompatButton
    private lateinit var btnLogin:TextView

    private lateinit var edtPass: TextInputEditText;private lateinit var edtEmail: TextInputEditText;private lateinit var edtPhoneNum:TextInputEditText
    private lateinit var edtName:TextInputEditText;private lateinit var edtRePass:TextInputEditText

    private lateinit var tilPass: TextInputLayout;private lateinit var tilEmail: TextInputLayout;private lateinit var tilPhoneNum:TextInputLayout
    private lateinit var tilName:TextInputLayout;private lateinit var tilRePass:TextInputLayout

    private var isValidUsername:Boolean = false;private  var isValidEmail:Boolean = false; private  var isValidPhoneNumber:Boolean = false;
    private  var isValidPassword:Boolean = false; private var arePasswordsMatching:Boolean = false
    private lateinit var pass:String;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Maping()
        addTextWatcher()
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Account/User")

        btnLogin.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        btnSignUp.setOnClickListener {
            if(areAllFieldsValid()){
                val name = edtName.text.toString().trim()
                val phone = edtPhoneNum.text.toString().trim()
                val email = edtEmail.text.toString().trim()
                val password = edtPass.text.toString().trim()
                val vpassword = edtRePass.text.toString().trim()
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
                            "Name" to name,
                            "Email" to email,
                            "Phone" to phone,
                            "Image" to "https://firebasestorage.googleapis.com/v0/b/testappbandodientu.appspot.com/o/Profile%2FenYxNXGIY4a0HxcX16yBzOZOzNa2.jpg?alt=media&token=6e21c196-90b4-4baa-b903-df0430994392",
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

        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private val EmailTextWatcher = object : TextWatcher {
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
    private val PasswordTextWatcher = object : TextWatcher {
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
    private val PhoneNumTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not needed
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            val phoneNumber = s.toString().trim()

            if (phoneNumber.isEmpty()) {
                isValidPhoneNumber = false
                tilPhoneNum.error = "Phone number cannot be empty"
            } else if (!isValidPhoneNumber(phoneNumber)) {
                isValidPhoneNumber = false
                tilPhoneNum.error = "Invalid Phone number"
            } else if(phoneNumber.length<11){
                isValidPhoneNumber = false
                tilPhoneNum.error = "Phonenumber cant be less than 11"
            }else if (phoneNumber.length>11){
                isValidPhoneNumber = false
                tilPhoneNum.error = "Phonenumber cant be more than 11"
            }
            else{
                isValidPhoneNumber = true
                tilPhoneNum.error = null
            }
        }
        override fun afterTextChanged(s: Editable?) {
            // Not needed
        }
        private fun isValidPhoneNumber(phone: String): Boolean {
            return Patterns.PHONE.matcher(phone).matches()
        }
    }
    private val UsernameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not needed for this example
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val username = s.toString()

            if (username.isEmpty()) {
                isValidUsername = false
                tilName.error = "Username cannot be empty"
            } else if (username.length < 4) {
                isValidUsername = false
                tilName.error = "Username must be at least 4 characters"
            } else {
                isValidUsername = true
                tilName.isErrorEnabled = false
            }
        }

        override fun afterTextChanged(s: Editable?) {
            // Not needed for this example
        }
    }
    private val RePasswordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not needed for this example
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val password = s.toString()

            if (password.isEmpty()) {
                arePasswordsMatching = false
                tilRePass.error = "Re-password cannot be empty"
            } else if (password.length < 6) {
                arePasswordsMatching = false
                tilRePass.error = "Re-password must be at least 6 characters"
            } else if (password != pass) {
                arePasswordsMatching = false
                tilRePass.error = "Re-password does not equal to password"
            } else {
                arePasswordsMatching = true
                tilRePass.isErrorEnabled = false
            }
        }

        override fun afterTextChanged(s: Editable?) {
            // Not needed for this example
        }
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    fun Maping(){

        edtName = findViewById<TextInputEditText>(R.id.edt_UserName_reg)
        edtEmail = findViewById<TextInputEditText>(R.id.edt_Email_reg)
        edtPhoneNum = findViewById<TextInputEditText>(R.id.edt_Sdt_reg)
        edtPass = findViewById<TextInputEditText>(R.id.edt_Password_reg)
        edtRePass = findViewById<TextInputEditText>(R.id.edt_RePassword_reg)

        tilName = findViewById<TextInputLayout>(R.id.til_UserName_reg)
        tilEmail = findViewById<TextInputLayout>(R.id.til_Email_reg)
        tilPhoneNum = findViewById<TextInputLayout>(R.id.til_Sdt_reg)
        tilPass = findViewById<TextInputLayout>(R.id.til_Password_reg)
        tilRePass = findViewById<TextInputLayout>(R.id.til_RePassword_reg)

        btnSignUp = findViewById<AppCompatButton>(R.id.btn_SignUp_Reg)
        btnLogin = findViewById<TextView>(R.id.tv_login_log)
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

    private fun areAllFieldsValid(): Boolean {
        return isValidUsername && isValidEmail && isValidPhoneNumber && isValidPassword && arePasswordsMatching
    }
    private fun addTextWatcher(){
        edtName.addTextChangedListener(UsernameTextWatcher)
        edtEmail.addTextChangedListener(EmailTextWatcher)
        edtPhoneNum.addTextChangedListener(PhoneNumTextWatcher)
        edtPass.addTextChangedListener(PasswordTextWatcher)
        edtRePass.addTextChangedListener(RePasswordTextWatcher)

    }
}