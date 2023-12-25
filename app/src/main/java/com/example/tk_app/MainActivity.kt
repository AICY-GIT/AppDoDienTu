package com.example.tk_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.tk_app.fragment.AccountFragment
import com.example.tk_app.fragment.CategoryFragment
import com.example.tk_app.fragment.HomeFragment
import com.example.tk_app.fragment.NewsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val homefragment = HomeFragment()
    private val categoryfragment = CategoryFragment()
    private val newsfragment = NewsFragment()
    private val accountfragment = AccountFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavView: BottomNavigationView = findViewById(R.id.bottomNavView)
        bottomNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_homefragment -> setFragment(homefragment)
                R.id.navigation_categoryfragment -> setFragment(categoryfragment)
                R.id.navigation_mewsfragment -> setFragment(newsfragment)
                R.id.navigation_accountfragment -> setFragment(accountfragment)
            }
            true
        }

        setFragment(homefragment) // Mặc định hiển thị Fragment 1 khi mở ứng dụng.
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
}
