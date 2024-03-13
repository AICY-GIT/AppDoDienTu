package com.example.tk_app.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.tk_app.R
import com.example.tk_app.classify_product.ClassifyActivity
import com.example.tk_app.classify_product.admin_products.ProductsActivity
import com.example.tk_app.voucher.CreateVoucherActivity

class AdminActivity : AppCompatActivity() {
    private lateinit var btn_Click_Classify : TextView
    private lateinit var btn_click_products : TextView
    private lateinit var btn_click_voucher :TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        btn_Click_Classify = findViewById(R.id.btn_click_classify)
        btn_Click_Classify.setOnClickListener {
            val intent = Intent(this, ClassifyActivity::class.java)
            startActivity(intent)
        }
        btn_click_products = findViewById(R.id.btn_click_products)
        btn_click_products.setOnClickListener {
            val intent = Intent(this, ProductsActivity::class.java)
            startActivity(intent)
        }
        btn_click_voucher = findViewById(R.id.btn_click_voucher)
        btn_click_voucher.setOnClickListener{
            val intent = Intent(this, CreateVoucherActivity::class.java)
            startActivity(intent)
        }
    }
}