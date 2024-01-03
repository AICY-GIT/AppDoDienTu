package com.example.tk_app.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.tk_app.R
import com.example.tk_app.account.EditAccountActivity
import com.example.tk_app.account.LoginActivity
import com.example.tk_app.classify_product.CartActivity
import com.example.tk_app.pay.PurchaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var proName: TextView
    private lateinit var proEmail: TextView
    private lateinit var proPhone: TextView
    private lateinit var btnSignout: LinearLayout
    private lateinit var btnChangePro: TextView
    private lateinit var proCart: TextView

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid.toString()

        proName = view.findViewById(R.id.proName)
        proEmail = view.findViewById(R.id.proEmail)
        proPhone = view.findViewById(R.id.proPhone)
        btnSignout = view.findViewById(R.id.btnSignout)
        btnChangePro = view.findViewById(R.id.btnChangePro)
        proCart=view.findViewById(R.id.proCart)

        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Account/User").child(userId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val name = dataSnapshot.child("Name").value.toString()
                    val email = dataSnapshot.child("Email").value.toString()
                    val phone = dataSnapshot.child("Phone").value.toString()
                    proName.text = name
                    proEmail.text = email
                    proPhone.text = phone
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
            }
        })

        btnSignout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
        }

        btnChangePro.setOnClickListener {
            val intent = Intent(requireActivity(), EditAccountActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("email", proEmail.text.toString())
            intent.putExtra("name", proName.text.toString())
            intent.putExtra("phone", proPhone.text.toString())
            startActivity(intent)
        }
        proCart.setOnClickListener {
            val intent = Intent(requireActivity(), CartActivity::class.java)
            startActivity(intent)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}