package ru.surf.learn2invest.ui.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.ActivityMainBinding
import ru.surf.learn2invest.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.ui.components.screens.sign_in.SignInActivity
import ru.surf.learn2invest.ui.components.screens.sign_up.SignUpActivity

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        skipSplash()
    }

    /**
     * Функция показа анимированного splash-скрина и проверки, есть ли у нас зарегистрированный пользователь
     */
    private fun skipSplash() {
        lifecycleScope.launch(Dispatchers.Main) {
            val intent =
                if (viewModel.databaseRepository.profile.lastName != "undefined" && viewModel.databaseRepository.profile.firstName != "undefined") {
                    runAnimatedText()

                    Intent(this@MainActivity, SignInActivity::class.java).also {
                        it.action = SignINActivityActions.SignIN.action
                    }
                } else {
                    Intent(this@MainActivity, SignUpActivity::class.java)
                }
            delay(2000)
            startActivity(intent)
            this@MainActivity.finish()
        }
    }

    /**
     * Функция показа анимации приветствия пользователя
     */
    private fun runAnimatedText() {
        (ContextCompat.getString(
            this, R.string.hello
        ) + ", ${viewModel.databaseRepository.profile.firstName}!").let {
            binding.splashTextView.text = it
        }
        binding.splashTextView.alpha = 0f
        val animator = ObjectAnimator.ofFloat(binding.splashTextView, "alpha", 0f, 1f)
        animator.duration = 2000 // Длительность анимации в миллисекундах
        animator.start()
    }
}
