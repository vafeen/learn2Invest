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
import ru.surf.learn2invest.R
import ru.surf.learn2invest.app.Learn2InvestApp
import ru.surf.learn2invest.databinding.ActivityMainBinding
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.database_components.entity.Profile
import ru.surf.learn2invest.noui.logs.Loher
import ru.surf.learn2invest.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.ui.components.screens.sign_in.SignInActivity
import ru.surf.learn2invest.ui.tests.data.insertProfileInCoroutineScope
import ru.surf.learn2invest.ui.tests.screens.DialogsTestActivity

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

//         data for testing (need to remove)
        insertProfileInCoroutineScope(lifecycleScope)


    }

    // Функция проверки, есть ли у нас зарегистрированный пользователь
    private fun skipSplash() {
        lifecycleScope.launch(Dispatchers.IO) {

            val deferred =
                async(Dispatchers.IO) { Learn2InvestApp.mainDB.profileDao().getProfile() }

            delay(1000)

            val intent = if (deferred.await().isNotEmpty()) {

                Learn2InvestApp.profile = deferred.await()[Learn2InvestApp.idOfProfile]

                Loher.d("profile = ${Learn2InvestApp.profile}")
                Intent(this@MainActivity, SignInActivity::class.java).let {
                    it.action = SignINActivityActions.SignIN.action

                    it
                }

            } else {
                Intent(this@MainActivity, SignInActivity::class.java)
//                TODO:Надь, вместо SignInActivity::class.java в этом блоке нужно активити с регистрацией
            }

            startActivity(intent)

            this@MainActivity.finish()

        }
    }
}
