package com.example.tk_app.classify_product

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.tk_app.R
import com.example.tk_app.classify_product.electronic_device.ElectronicDeviceActivity
import com.example.tk_app.classify_product.men_fashion.MenFashionActivity
import com.example.tk_app.classify_product.phones_accessories.PhonesAccessoriesActivity
import com.example.tk_app.classify_product.women_fashion.WomenFashionActivity

class ClassifyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classify)
        val btn_Click_Add_An_Classify = findViewById<TextView>(R.id.btn_click_add_men_classify)
        val btn_Click_Add_An_Classify2 = findViewById<TextView>(R.id.btn_click_add_women_classify)
        val btn_click_add_Phone_classify = findViewById<TextView>(R.id.btn_click_add_phone_classify)
        val btn_click_add_Electronic_classify = findViewById<TextView>(R.id.btn_click_add_electronic_classify)

        btn_Click_Add_An_Classify.setOnClickListener {
            val intent = Intent(this, MenFashionActivity::class.java)
            startActivity(intent)
        }
        btn_Click_Add_An_Classify2.setOnClickListener {
            val intent = Intent(this, WomenFashionActivity::class.java)
            startActivity(intent)
        }
        btn_click_add_Phone_classify.setOnClickListener {
            val intent = Intent(this, PhonesAccessoriesActivity::class.java)
            startActivity(intent)
        }
        btn_click_add_Electronic_classify.setOnClickListener {
            val intent = Intent(this, ElectronicDeviceActivity::class.java)
            startActivity(intent)
        }
    }
}