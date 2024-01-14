package com.example.tk_app.classify_product

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.tk_app.R
import com.example.tk_app.classify_product.watch.WatchActivity
import com.example.tk_app.classify_product.phones.PhoneActivity
import com.example.tk_app.classify_product.earphones_accessories.EarPhoneActivity
import com.example.tk_app.classify_product.accessory.AccessoryActivity

class ClassifyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classify)
        val btn_Click_Add_An_Classify = findViewById<TextView>(R.id.btn_click_add_phone_classify)
        val btn_Click_Add_An_Classify2 = findViewById<TextView>(R.id.btn_click_add_accessory_classify)
        val btn_click_add_Phone_classify = findViewById<TextView>(R.id.btn_click_add_earphone_classify)
        val btn_click_add_Electronic_classify = findViewById<TextView>(R.id.btn_click_add_watch_classify)

        btn_Click_Add_An_Classify.setOnClickListener {
            val intent = Intent(this, PhoneActivity::class.java)
            startActivity(intent)
        }
        btn_Click_Add_An_Classify2.setOnClickListener {
            val intent = Intent(this, AccessoryActivity::class.java)
            startActivity(intent)
        }
        btn_click_add_Phone_classify.setOnClickListener {
            val intent = Intent(this, EarPhoneActivity::class.java)
            startActivity(intent)
        }
        btn_click_add_Electronic_classify.setOnClickListener {
            val intent = Intent(this, WatchActivity::class.java)
            startActivity(intent)
        }
    }
}