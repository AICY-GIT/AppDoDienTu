package com.example.tk_app.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tk_app.R
import com.example.tk_app.account.SearchActivity
import com.example.tk_app.classify_product.CartActivity
import com.example.tk_app.classify_product.accessory.ProductAccessory
import com.example.tk_app.classify_product.accessory.ProductsAccessoryAdapter
import com.example.tk_app.classify_product.earphones.EarPhonesAccessoriesAdapter
import com.example.tk_app.classify_product.earphones.ProductEarPhonesAccessories
import com.example.tk_app.classify_product.phones.ProductPhone
import com.example.tk_app.classify_product.phones.ProductsPhoneAdapter
import com.example.tk_app.classify_product.watch.ProductWatch
import com.example.tk_app.classify_product.watch.WatchAdapter
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
 * Use the [CategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var productAdapter: ProductsPhoneAdapter
    private lateinit var productAdapter2: ProductsAccessoryAdapter
    private lateinit var productAdapter3: EarPhonesAccessoriesAdapter
    private lateinit var productAdapter4: WatchAdapter
    private val productsList = mutableListOf<ProductPhone>()
    private val productsList2 = mutableListOf<ProductAccessory>()
    private val productsList3 = mutableListOf<ProductEarPhonesAccessories>()
    private val productsList4 = mutableListOf<ProductWatch>()

    private lateinit var btn_Click_On_Cart: TextView
    private lateinit var btn_Click_Search: TextView
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
        val view = inflater.inflate(R.layout.fragment_category, container, false)
        val show_List_Men_Fashion = view.findViewById<RecyclerView>(R.id.rv_home_phone)
        val show_List_Women_Fashion = view.findViewById<RecyclerView>(R.id.rv_home_accessory)
        val show_List_Phone = view.findViewById<RecyclerView>(R.id.rv_home_earrphone)
        val show_List_Electronic = view.findViewById<RecyclerView>(R.id.rv_home_watch)
        show_List_Men_Fashion.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        productAdapter = ProductsPhoneAdapter(productsList)
        show_List_Men_Fashion.adapter = productAdapter
        // Gọi hàm để lấy dữ liệu từ Firebase Realtime Database
        fetchProductData()

        show_List_Women_Fashion.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        productAdapter2 = ProductsAccessoryAdapter(productsList2)
        show_List_Women_Fashion.adapter = productAdapter2
        fetchProductData2()

        show_List_Phone.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        productAdapter3 = EarPhonesAccessoriesAdapter(productsList3)
        show_List_Phone.adapter = productAdapter3
        fetchProductData3()

        show_List_Electronic.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        productAdapter4 = WatchAdapter(productsList4)
        show_List_Electronic.adapter = productAdapter4
        // Gọi hàm để lấy dữ liệu từ Firebase Realtime Database
        fetchProductData4()
        //cart
        btn_Click_On_Cart = view.findViewById(R.id.btn_click_on_cart)
        btn_Click_On_Cart.setOnClickListener {
            // Chuyển đến trang CartActivity khi nút "Go to Cart" được nhấn
            val intent = Intent(requireContext(), CartActivity::class.java)
            startActivity(intent)
        }

        btn_Click_Search = view.findViewById(R.id.btn_click_search)
        btn_Click_Search.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }


        return view
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
                    val quantity = productSnapshot.child("quantoty").value.toString()
                    val origin = productSnapshot.child("origin").value.toString()

                    //thêm rate
                    val rateString = productSnapshot.child("rate").value.toString()

                    val rateDouble = rateString.toDoubleOrNull() ?: 0.0  // Mặc định là 0.0 nếu chuyển đổi thất bại
                    val product = ProductPhone(productId, imageUrl, material, price, name, type, details, origin, quantity,rateDouble)
                    productsList.add(product)
                }
                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu cần
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

                    val rateDouble = rateString.toDoubleOrNull() ?: 0.0  // Mặc định là 0.0 nếu chuyển đổi thất bại

                    val product2 = ProductAccessory(productId, imageUrl, material, price, name, type, details, origin, quantity, rateDouble)
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

                    val rateDouble = rateString.toDoubleOrNull() ?: 0.0  // Mặc định là 0.0 nếu chuyển đổi thất bại
                    val product3 = ProductEarPhonesAccessories(productId, imageUrl, material, price, name, type, details, origin, quantity, rateDouble)
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

                    val rateDouble = rateString.toDoubleOrNull() ?: 0.0  // Mặc định là 0.0 nếu chuyển đổi thất bại
                    val product4 = ProductWatch(productId, imageUrl, material, price, name, type, details, origin, quantity,rateDouble)
                    productsList4.add(product4)
                }
                productAdapter4.notifyDataSetChanged() // Cập nhật adapter thích hợp
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu cần
            }
        })
    }
    fun reloadFragment() {
        fetchProductData()
        fetchProductData2()
        fetchProductData3()
        fetchProductData4()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CategoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}