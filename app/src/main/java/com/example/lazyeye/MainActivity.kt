package com.example.lazyeye

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNav: BottomNavigationView = findViewById(R.id.my_bottom_navigation)
        val hostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView3) as NavHostFragment
        val navController = hostFragment.navController
        NavigationUI.setupWithNavController(bottomNav, navController)

    }
}