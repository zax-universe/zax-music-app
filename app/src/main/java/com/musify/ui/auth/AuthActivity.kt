package com.musify.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.musify.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.authNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        val mode = intent.getStringExtra("mode") ?: "login"
        if (mode == "register") {
            navController.navigate(R.id.registerFragment)
        }
    }
}
