package com.example.tk_app.classify_product.admin_products

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tk_app.R
import com.example.tk_app.classify_product.accessory.AdminProductsAccessoryAdapter
import com.example.tk_app.classify_product.accessory.ProductAccessory
import com.example.tk_app.classify_product.accessory.ProductsAccessoryAdapter
import com.example.tk_app.classify_product.earphones.AdminProductsEarPhoneAdapter
import com.example.tk_app.classify_product.earphones.EarPhonesAccessoriesAdapter
import com.example.tk_app.classify_product.earphones.ProductEarPhonesAccessories
import com.example.tk_app.classify_product.phones.AdminProductsPhoneAdapter
import com.example.tk_app.classify_product.phones.ProductPhone
import com.example.tk_app.classify_product.phones.ProductsPhoneAdapter
import com.example.tk_app.classify_product.watch.AdminProductsWatchAdapter
import com.example.tk_app.classify_product.watch.ProductWatch
import com.example.tk_app.classify_product.watch.WatchAdapter
import com.google.firebase.database.*

class ProductListActivity : AppCompatActivity() {
    private lateinit var productAdapter: AdminProductsPhoneAdapter
    private lateinit var productAdapter2: AdminProductsAccessoryAdapter
    private lateinit var productAdapter3: AdminProductsEarPhoneAdapter
    private lateinit var productAdapter4: AdminProductsWatchAdapter
    private val productsList = mutableListOf<ProductPhone>()
    private val productsList2 = mutableListOf<ProductAccessory>()
    private val productsList3 = mutableListOf<ProductEarPhonesAccessories>()
    private val productsList4 = mutableListOf<ProductWatch>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val show_List_Men_Fashion = findViewById<RecyclerView>(R.id.rv_home_phone)
        val show_List_Women_Fashion = findViewById<RecyclerView>(R.id.rv_home_accessory)
        val show_List_Phone = findViewById<RecyclerView>(R.id.rv_home_earrphone)
        val show_List_Electronic = findViewById<RecyclerView>(R.id.rv_home_watch)

        show_List_Men_Fashion.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        productAdapter = AdminProductsPhoneAdapter(productsList)
        show_List_Men_Fashion.adapter = productAdapter

        show_List_Women_Fashion.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        productAdapter2 = AdminProductsAccessoryAdapter(productsList2)
        show_List_Women_Fashion.adapter = productAdapter2

        show_List_Phone.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        productAdapter3 = AdminProductsEarPhoneAdapter(productsList3)
        show_List_Phone.adapter = productAdapter3

        show_List_Electronic.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        productAdapter4 = AdminProductsWatchAdapter(productsList4)
        show_List_Electronic.adapter = productAdapter4

        // Gọi hàm để lấy dữ liệu từ Firebase Realtime Database
        fetchProductData()
        fetchProductData2()
        fetchProductData3()
        fetchProductData4()

    }

    private fun fetchProductData() {
        val uid = "HJqF0S5j3cM7VImvgyTjxhE4D6e2"
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("Product").child("Classify")
                .child("Phones")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productsList.clear()
                for (productSnapshot in snapshot.children) {

                    val productId = productSnapshot.key
                    val imageUrl = productSnapshot.child("imageUrl").value.toString()
                    val material = productSnapshot.child("material").value.toString()
                    val price = productSnapshot.child("price").value.toString()
                    val name = productSnapshot.child("name").value.toString()
                    val type = productSnapshot.child("type").value.toString()
                    val details = productSnapshot.child("details").value.toString()
                    val quantity = productSnapshot.child("quantity").value.toString()
                    val origin = productSnapshot.child("origin").value.toString()

                    //thêm rate
                    val rateString = productSnapshot.child("rate").value.toString()

                    val rateDouble = rateString.toDoubleOrNull() ?: 0.0
                    val product = ProductPhone(
                        productId,
                        imageUrl,
                        material,
                        price,
                        name,
                        type,
                        details,
                        origin,
                        quantity,
                        rateDouble
                    )
                    productsList.add(product)


                }
                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu cần
                Toast.makeText(
                    this@ProductListActivity,
                    "Database Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun fetchProductData2() {
        val uid = "HJqF0S5j3cM7VImvgyTjxhE4D6e2"
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("Product").child("Classify")
                .child("Accessory")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productsList2.clear()
                for (productSnapshot in snapshot.children) {
                    val productId = productSnapshot.key
                    val imageUrl = productSnapshot.child("imageUrl").value.toString()
                    val material = productSnapshot.child("material").value.toString()
                    val price = productSnapshot.child("price").value.toString()
                    val name = productSnapshot.child("name").value.toString()
                    val type = productSnapshot.child("type").value.toString()
                    val details = productSnapshot.child("details").value.toString()
                    val quantity = productSnapshot.child("quantity").value.toString()
                    val origin = productSnapshot.child("origin").value.toString()

                    //thêm rate
                    val rateString = productSnapshot.child("rate").value.toString()

                    val rateDouble = rateString.toDoubleOrNull()
                        ?: 0.0  // Mặc định là 0.0 nếu chuyển đổi thất bại

                    val product2 = ProductAccessory(
                        productId,
                        imageUrl,
                        material,
                        price,
                        name,
                        type,
                        details,
                        origin,
                        quantity,
                        rateDouble
                    )
                    productsList2.add(product2)
                }
                productAdapter2.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu cần
            }
        })
    }

    private fun fetchProductData3() {
        val uid = "HJqF0S5j3cM7VImvgyTjxhE4D6e2"
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("Product").child("Classify")
                .child("Ear_Phones")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productsList3.clear()
                for (productSnapshot in snapshot.children) {
                    val productId = productSnapshot.key
                    val imageUrl = productSnapshot.child("imageUrl").value.toString()
                    val material = productSnapshot.child("material").value.toString()
                    val price = productSnapshot.child("price").value.toString()
                    val name = productSnapshot.child("name").value.toString()
                    val type = productSnapshot.child("type").value.toString()
                    val details = productSnapshot.child("details").value.toString()
                    val quantity = productSnapshot.child("quantity").value.toString()
                    val origin = productSnapshot.child("origin").value.toString()

                    //thêm rate
                    val rateString = productSnapshot.child("rate").value.toString()

                    val rateDouble = rateString.toDoubleOrNull()
                        ?: 0.0  // Mặc định là 0.0 nếu chuyển đổi thất bại
                    val product3 = ProductEarPhonesAccessories(
                        productId,
                        imageUrl,
                        material,
                        price,
                        name,
                        type,
                        details,
                        origin,
                        quantity,
                        rateDouble
                    )
                    productsList3.add(product3)
                }
                productAdapter3.notifyDataSetChanged() // Cập nhật adapter thích hợp
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu cần
            }
        })
    }

    private fun fetchProductData4() {
        val uid = "HJqF0S5j3cM7VImvgyTjxhE4D6e2"
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("Product").child("Classify")
                .child("Watch")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productsList4.clear()
                for (productSnapshot in snapshot.children) {
                    val productId = productSnapshot.key
                    val imageUrl = productSnapshot.child("imageUrl").value.toString()
                    val material = productSnapshot.child("material").value.toString()
                    val price = productSnapshot.child("price").value.toString()
                    val name = productSnapshot.child("name").value.toString()
                    val type = productSnapshot.child("type").value.toString()
                    val details = productSnapshot.child("details").value.toString()
                    val quantity = productSnapshot.child("quantity").value.toString()
                    val origin = productSnapshot.child("origin").value.toString()
                    //thêm rate
                    val rateString = productSnapshot.child("rate").value.toString()

                    val rateDouble = rateString.toDoubleOrNull()
                        ?: 0.0  // Mặc định là 0.0 nếu chuyển đổi thất bại
                    val product4 = ProductWatch(
                        productId,
                        imageUrl,
                        material,
                        price,
                        name,
                        type,
                        details,
                        origin,
                        quantity,
                        rateDouble
                    )
                    productsList4.add(product4)
                }
                productAdapter4.notifyDataSetChanged() // Cập nhật adapter thích hợp
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu cần
            }
        })
    }

    // Tương tự cho các hàm fetchProductData2, fetchProductData3 và fetchProductData4
}
