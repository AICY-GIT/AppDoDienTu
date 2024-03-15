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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


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
    private lateinit var voucher: VoucherModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_voucher)
        Mapping()
        btn_voucher_create.setOnClickListener{
            val dateStart = getDateFromDatePicker(dp_voucher_dateStart)
            val dateEnd = getDateFromDatePicker(dp_voucher_dateEnd)
            if (checkNull()) {
                if (dateStart < dateEnd) {
                    val codeToCheck = edt_voucher_code.text?.toString()
                    CoroutineScope(Dispatchers.Main).launch {
                        if (!codeChecker(codeToCheck)) {
                            uploadVoucherToFirebase(voucher)
                        } else {
                            Toast.makeText(this@CreateVoucherActivity, "Code already exists", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "DateStart is later than DateEnd", Toast.LENGTH_SHORT).show()
                }
            }
        }
        voucher = VoucherModel()
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
    private fun checkNull():Boolean{
        var result=false;
        val name = edt_voucher_name.text?.toString()
        val code = edt_voucher_code.text?.toString()
        val discountPercentage = edt_voucher_discountPercentage.text?.toString()
        val maxDiscount = edt_voucher_maxDiscount.text?.toString()
        val amount = edt_voucher_amount.text?.toString()
        val dateStart = getDateFromDatePicker(dp_voucher_dateStart)
        val dateEnd = getDateFromDatePicker(dp_voucher_dateEnd)

        if (name.isNullOrEmpty() ||
            code.isNullOrEmpty() ||
            discountPercentage.isNullOrEmpty() ||
            maxDiscount.isNullOrEmpty() ||
            amount.isNullOrEmpty() ||
            dateStart == null ||
            dateEnd == null) {
            Toast.makeText(this, "please fill all field", Toast.LENGTH_SHORT).show()
            return result
        } else {
            result=true
            voucher.voucherId = null
            voucher.name = name
            voucher.code = code
            voucher.discountPercentage = discountPercentage // Assuming discountPercentage is Double
            voucher.maxDiscount = maxDiscount // Assuming maxDiscount is Double
            voucher.amount = amount // Assuming amount is Double
            voucher.dateStart = dateStart
            voucher.dateEnd = dateEnd
            return result
        }
    }
    private suspend fun codeChecker(codeToCheck: String?): Boolean {
        val database = FirebaseDatabase.getInstance()
        val voucherRef = database.getReference("voucher")

        var result = false

        // Use a suspendCoroutine to wait for the asynchronous result
        try {
            result = suspendCoroutine { continuation ->
                voucherRef.orderByChild("code").equalTo(codeToCheck).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        continuation.resume(dataSnapshot.exists())
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        continuation.resume(false)
                    }
                })
            }
        } catch (e: Exception) {
            // Handle error
        }

        return result
    }
}