package ru.surf.learn2invest.ui.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

    //    private lateinit var context: Context
    private val viewModel: MainActivityViewModel by viewModels()
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
