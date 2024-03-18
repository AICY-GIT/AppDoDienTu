package com.example.tk_app.fragment

import SearchAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.tk_app.R
import com.example.tk_app.classify_product.CartActivity
import com.example.tk_app.classify_product.CartItemModel

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var iv_home_cart: ImageView
    private lateinit var tv_click_show_all: TextView
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter
    private var productList: MutableList<CartItemModel> = mutableListOf() // Khởi tạo trực tiếp
    private lateinit var btn_home_dienthoai: LinearLayout
    private lateinit var btn_home_phukien: LinearLayout
    private lateinit var btn_home_tainghe: LinearLayout
    private lateinit var btn_home_dongho: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.searchResultRecyclerView)
        searchAdapter = SearchAdapter(requireContext(), productList)
        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
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
        // Initialize SearchView
        searchView = view.findViewById(R.id.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                updateSearchList(newText.orEmpty())
                return true
            }
        })
        val recyclerViewContainer = view.findViewById<LinearLayout>(R.id.recyclerViewContainer)
        searchView.setOnClickListener {
            recyclerViewContainer.visibility = View.GONE
        }
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            recyclerViewContainer.visibility = View.VISIBLE
        }

        val tv_click_show_all = view.findViewById<TextView>(R.id.tv_show_all)
        tv_click_show_all.setOnClickListener {
            val categoryFragment = CategoryFragment()
            val fragmentManager = requireFragmentManager()
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, categoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        val btnHomeDienthoai = view.findViewById<LinearLayout>(R.id.ln_home_dienthoai)
        btnHomeDienthoai.setOnClickListener {
            onCategorySelected("1") // 1 represents điện thoại category
        }

        val btnHomePhukien = view.findViewById<LinearLayout>(R.id.ln_home_phukien)
        btnHomePhukien.setOnClickListener {
            onCategorySelected("2") // 2 represents phụ kiện category
        }

        val btnHomeTainghe = view.findViewById<LinearLayout>(R.id.ln_home_tainghe)
        btnHomeTainghe.setOnClickListener {
            onCategorySelected("3") // 3 represents tai nghe category
        }

        val btnHomeDongho = view.findViewById<LinearLayout>(R.id.ln_home_dongho)
        btnHomeDongho.setOnClickListener {
            onCategorySelected("4") // 4 represents đồng hồ category
        }
        val iv_home_cart = view.findViewById<ImageView>(R.id.iv_home_cart)
        iv_home_cart.setOnClickListener {
            val intent = Intent(requireContext(), CartActivity::class.java)
            startActivity(intent)
        }

        return view

    }


    private fun updateSearchList(query: String) {
        val filteredList = productList.filter {
            it.name?.toLowerCase()?.contains(query.toLowerCase()) == true
        }.take(3) // Lấy tối đa 5 sản phẩm

        searchAdapter.updateSearchList(filteredList)

        // Log để kiểm tra xem hàm này có được gọi không
        Log.d(
            "HomeFragment",
            "updateSearchList called with query: $query, items: ${filteredList.size}"
        )
    }

    fun onCategorySelected(categoryId: String?) {
        val categoryFragment = CategoryFragment()
        val bundle = Bundle()
        bundle.putString("categoryId", categoryId)
        categoryFragment.arguments = bundle

        val fragmentManager = requireFragmentManager()
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, categoryFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String?) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    param2?.let { putString(ARG_PARAM2, it) } // Kiểm tra nullability trước khi thêm vào Bundle
                }
            }
    }
}