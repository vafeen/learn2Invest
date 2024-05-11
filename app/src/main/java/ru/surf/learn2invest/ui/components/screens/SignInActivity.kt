package ru.surf.learn2invest.ui.components.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.surf.learn2invest.databinding.ActivitySigninBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigninBinding.inflate(layoutInflater)

        setContentView(binding.root)

    }

}