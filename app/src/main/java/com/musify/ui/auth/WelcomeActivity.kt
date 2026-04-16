package com.musify.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.musify.R
import com.musify.databinding.ActivityWelcomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java).apply {
                putExtra("mode", "register")
            })
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java).apply {
                putExtra("mode", "login")
            })
        }
    }
}
