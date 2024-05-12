package ru.surf.learn2invest.ui.components.screens

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.database_components.entity.Profile
import ru.surf.learn2invest.databinding.ActivitySigninBinding
import ru.surf.learn2invest.main.Learn2InvestApp

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding

    private var pinCode: String = ""

    private lateinit var user: Profile
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigninBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initHash()

        initListeners()
    }

    private fun initHash() {
        lifecycleScope.launch(Dispatchers.IO) {
            user = Learn2InvestApp.mainDB.profileDao().getProfile()[0]
        }
    }

    private fun paintDots() {
        when (pinCode.length) {
            1 -> {
                binding.dot1.drawable.setTint(Color.BLACK)
            }

            2 -> {
                binding.dot1.drawable.setTint(Color.BLACK)

                binding.dot2.drawable.setTint(Color.BLACK)
            }

            3 -> {
                binding.dot1.drawable.setTint(Color.BLACK)

                binding.dot2.drawable.setTint(Color.BLACK)

                binding.dot3.drawable.setTint(Color.BLACK)
            }

            4 -> {
                binding.dot1.drawable.setTint(Color.BLACK)

                binding.dot2.drawable.setTint(Color.BLACK)

                binding.dot3.drawable.setTint(Color.BLACK)

                binding.dot4.drawable.setTint(Color.BLACK)

                if (PasswordHasher(user = user).verify(pinCode)) {
                    Toast.makeText(this, "PIN верен", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "PIN не верен", Toast.LENGTH_SHORT).show()
                }



                lifecycleScope.launch(Dispatchers.Main) {
                    delay(500)

                    pinCode = ""

                    //рекурсия тут ок, т.к. второй раз сюда просто так не попасть
                    paintDots()
                }

            }

            else -> {
                binding.dot1.drawable.setTint(Color.WHITE)

                binding.dot2.drawable.setTint(Color.WHITE)

                binding.dot3.drawable.setTint(Color.WHITE)

                binding.dot4.drawable.setTint(Color.WHITE)
            }
        }
    }

    private fun updatePin(num: String) {
        if (pinCode.length < 4) {
            pinCode += num
            Log.d("App", "pinCode = $pinCode")
            paintDots()
        }
    }

    private fun initListeners() {
        binding.passButton0.setOnClickListener {
            updatePin("0")
        }

        binding.passButton1.setOnClickListener {
            updatePin("1")
        }

        binding.passButton2.setOnClickListener {
            updatePin("2")
        }

        binding.passButton3.setOnClickListener {
            updatePin("3")
        }

        binding.passButton4.setOnClickListener {
            updatePin("4")
        }

        binding.passButton5.setOnClickListener {
            updatePin("5")
        }

        binding.passButton6.setOnClickListener {
            updatePin("6")
        }

        binding.passButton7.setOnClickListener {
            updatePin("7")
        }

        binding.passButton8.setOnClickListener {
            updatePin("8")
        }

        binding.passButton9.setOnClickListener {
            updatePin("9")
        }

    }
}