package ru.surf.learn2invest.ui.main

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.ActivityMainBinding
import ru.surf.learn2invest.noui.database_components.DatabaseRepository.profile
import ru.surf.learn2invest.ui.components.screens.SignUpActivity
import ru.surf.learn2invest.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.ui.components.screens.sign_in.SignInActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        context = this

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        skipSplash()

    }

    // Функция проверки, есть ли у нас зарегистрированный пользователь
    private fun skipSplash() {
        lifecycleScope.launch(Dispatchers.IO) {
            val intent =
                if (profile.lastName != "undefined" && profile.firstName != "undefined") {
                    withContext(Dispatchers.Main) {
                        runAnimatedText()
                        delay(2000)
                    }

                    Intent(this@MainActivity, SignInActivity::class.java).also {
                        it.action = SignINActivityActions.SignIN.action
                    }
                } else {
                    Intent(this@MainActivity, SignUpActivity::class.java)
                }

            delay(1000)

            startActivity(intent)

            this@MainActivity.finish()
        }
    }

    private fun runAnimatedText() {
        binding.splashTextView.text = "Здравствуй, ${profile.firstName}!"
        binding.splashTextView.alpha = 0f

        val animator = ObjectAnimator.ofFloat(binding.splashTextView, "alpha", 0f, 1f)
        animator.duration = 2000 // Длительность анимации в миллисекундах
        animator.start()
    }
}
