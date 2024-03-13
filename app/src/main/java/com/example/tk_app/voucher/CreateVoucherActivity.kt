package com.example.tk_app.voucher

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.tk_app.R
import android.widget.DatePicker
import android.widget.Toast
import com.example.tk_app.MainActivity
import com.example.tk_app.VoucherModel
import com.example.tk_app.account.AdminActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class CreateVoucherActivity : AppCompatActivity() {
    private lateinit var edt_voucher_name:EditText
    private lateinit var edt_voucher_code:EditText
    private lateinit var edt_voucher_discountPercentage:EditText
    private lateinit var edt_voucher_maxDiscount:EditText
    private lateinit var edt_voucher_amount:EditText
    private lateinit var dp_voucher_dateStart:DatePicker
    private lateinit var dp_voucher_dateEnd:DatePicker
    private lateinit var btn_voucher_create:Button
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_voucher)
        Mapping()
        btn_voucher_create.setOnClickListener{
            val voucher= CreateVoucher()
            uploadVoucherToFirebase(voucher)
        }
    }
    private fun Mapping(){
        edt_voucher_name = findViewById(R.id.edt_voucher_name)
        edt_voucher_code = findViewById(R.id.edt_voucher_code)
        edt_voucher_discountPercentage = findViewById(R.id.edt_voucher_discountPercentage)
        edt_voucher_maxDiscount = findViewById(R.id.edt_voucher_maxDiscount)
        edt_voucher_amount = findViewById(R.id.edt_voucher_amount)
        dp_voucher_dateStart = findViewById(R.id.dp_voucher_dateStart)
        dp_voucher_dateEnd = findViewById(R.id.dp_voucher_dateEnd)
        btn_voucher_create=findViewById(R.id.btn_voucher_create)
    }
    private fun CreateVoucher(): VoucherModel {
        val voucher = VoucherModel(
            voucherId = null, // You may generate a unique ID here, or Firebase will generate one when pushing to the database
            name = edt_voucher_name.text.toString(),
            code = edt_voucher_code.text.toString(),
            dicountPercentage = edt_voucher_discountPercentage.text.toString(),
            maxDiscount = edt_voucher_maxDiscount.text.toString(),
            amount = edt_voucher_amount.text.toString(),
            dateStart = getDateFromDatePicker(dp_voucher_dateStart),
            dateEnd = getDateFromDatePicker(dp_voucher_dateEnd)
        )
        return voucher
    }

    private fun getDateFromDatePicker(datePicker: DatePicker): String {
        val day = datePicker.dayOfMonth
        val month = datePicker.month + 1 // Month is 0-indexed
        val year = datePicker.year

        return "$year-$month-$day"
    }
    private fun uploadVoucherToFirebase(voucher:VoucherModel) {
        databaseReference = FirebaseDatabase.getInstance().reference
        val voucherId = databaseReference.push().key // Generate a unique key for the voucher

        if (voucherId != null) {
            // Upload the voucher to Firebase
            voucher.voucherId=voucherId
            databaseReference.child("voucher").child(voucherId).setValue(voucher)
                .addOnSuccessListener {
                    // Handle success (optional)
                    Toast.makeText(this, "Voucher created and uploaded successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CreateVoucherActivity, AdminActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    // Handle failure (optional)
                    Toast.makeText(this, "Failed to upload voucher", Toast.LENGTH_SHORT).show()
                }
        }
    }
}