package ru.surf.learn2invest.ui.components.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.surf.learn2invest.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}