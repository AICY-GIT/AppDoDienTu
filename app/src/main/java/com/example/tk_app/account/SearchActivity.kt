package com.example.tk_app.account

import SearchAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tk_app.R
import com.example.tk_app.classify_product.CartItemModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter
    private var productList: MutableList<CartItemModel> = ArrayList()
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.recyclerView1)
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchAdapter = SearchAdapter(this, productList)
        recyclerView.adapter = searchAdapter

        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("Product").child("Classify")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (categorySnapshot in snapshot.children) {
                    for (productSnapshot in categorySnapshot.children) {
                        val product = productSnapshot.getValue(CartItemModel::class.java)
                        if (product != null) {
                            productList.add(product)
                            product.productId = productSnapshot.key
                        }
                    }
                }
                updateSearchList("") // Hiển thị tất cả sản phẩm ban đầu
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi truy cập cơ sở dữ liệu
            }
        })

        searchView = findViewById(R.id.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                updateSearchList(newText.orEmpty())
                return true
            }
        })
    }

    private fun updateSearchList(query: String) {
        val filteredList = productList.filter {
            it.name?.toLowerCase()?.contains(query.toLowerCase()) == true
        }
        searchAdapter.updateSearchList(filteredList)
    }
}
