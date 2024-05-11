package ru.surf.learn2invest.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.ActivityMainBinding
import ru.surf.learn2invest.ui.components.screens.SignInActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        skipSplash()
    }

    private fun skipSplash() {
        lifecycleScope.launch(Dispatchers.IO) {

            val deferred =
                async(Dispatchers.IO) { Learn2InvestApp.mainDB.profileDao().getProfile() }

            delay(800)

            val intent = Intent(this@MainActivity, SignInActivity::class.java)

            if (deferred.await().isEmpty()) {
                startActivity(intent)
            }

            this@MainActivity.finish()

        }
    }
}
