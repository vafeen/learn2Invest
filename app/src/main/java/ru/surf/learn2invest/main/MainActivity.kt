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
import ru.surf.learn2invest.databinding.ActivityMainBinding
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.database_components.entity.Profile
import ru.surf.learn2invest.ui.components.screens.SignINActivityActions
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

//         data for testing (need to remove)
        lifecycleScope.launch(Dispatchers.IO) {
            Learn2InvestApp.mainDB.profileDao().insertAll(
                Profile(
                    id = 0,
                    firstName = "A",
                    lastName = "Vafeen",
                    pin = 0,
                    notification = true,
                    biometry = true,
                    confirmDeal = true,
                    fiatBalance = 0,
                    assetBalance = 0,
                ).let {
                    it.copy(hash = PasswordHasher(user = it).passwordToHash("0000"))
                }
            )
        }


    }

    // Функция проверки, есть ли у нас зарегистрированный пользователь
    private fun skipSplash() {
        lifecycleScope.launch(Dispatchers.IO) {

            val deferred =
                async(Dispatchers.IO) { Learn2InvestApp.mainDB.profileDao().getProfile() }

            delay(1000)

            val intent = if (deferred.await().isNotEmpty()) {

                Intent(this@MainActivity, SignInActivity::class.java).let {
                    it.action = SignINActivityActions.SignUP.action

                    it
                }

            } else {
                Intent(this@MainActivity, SignInActivity::class.java)
//                TODO:Володь, вместо SignInActivity::class.java в этом блоке нужно активити с регистрацией
            }

            startActivity(intent)

            this@MainActivity.finish()

        }
    }
}
