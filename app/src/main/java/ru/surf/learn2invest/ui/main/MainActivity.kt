package ru.surf.learn2invest.ui.main

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.app.App.Companion.mainDB
import ru.surf.learn2invest.app.App.Companion.profile
import ru.surf.learn2invest.databinding.ActivityMainBinding
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.database_components.entity.Profile
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
            mainDB.profileDao().getAllAsFlow().collect { profList ->
                if (profList.isNotEmpty()) {
                    profile = profList[App.idOfProfile]
                    Log.d("profile", "profile APP no else = $profile ")

                    val undef = "undefined"

                    val intent = if (App.profile.firstName == undef &&
                        App.profile.lastName == undef
                    ) {
                        delay(1500)
                        Intent(this@MainActivity, SignUpActivity::class.java)
                    } else {
                        withContext(Dispatchers.Main) {
                            runAnimatedText()
                            delay(2000)
                        }

                        Intent(this@MainActivity, SignInActivity::class.java).let {
                            it.action = SignINActivityActions.SignIN.action

                            it
                        }
                    }
                } else {
                    profile = Profile(
                        id = 0,
                        firstName = "undefined",
                        lastName = "undefined",
                        biometry = false,
                        fiatBalance = 50540f,
                        assetBalance = 0f,
                        hash = PasswordHasher(
                            firstName = "undefined",
                            lastName = "undefined"
                        ).passwordToHash("0000"),
                        tradingPasswordHash = PasswordHasher(
                            firstName = "undefined",
                            lastName = "undefined"
                        ).passwordToHash("1235789")
                    )

                    mainDB.profileDao().insertAll(profile)

                    Log.d("profile", "profile APP else  = $profile ")

                }

                delay(1000)

                val undef = "undefined"

                val intent = if (profile.firstName == undef &&
                    profile.lastName == undef
                ) {

                    Intent(this@MainActivity, SignUpActivity::class.java)

                } else {

                    Intent(this@MainActivity, SignInActivity::class.java).let {
                        it.action = SignINActivityActions.SignIN.action

                        it
                    }

                }

                startActivity(intent)

                this@MainActivity.finish()
            }
        }
    }

    fun runAnimatedText() {
        binding.splashTextView.text = "Здравствуй, ${App.profile.firstName}!"
        binding.splashTextView.alpha = 0f

        val animator = ObjectAnimator.ofFloat(binding.splashTextView, "alpha", 0f, 1f)
        animator.duration = 2000 // Длительность анимации в миллисекундах
        animator.start()
    }
}
