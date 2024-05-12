package ru.surf.learn2invest.ui.components.screens

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.databinding.ActivitySigninBinding
import ru.surf.learn2invest.main.Learn2InvestApp
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.database_components.entity.Profile


import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import java.util.concurrent.Executor


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding

    private var pinCode: String = ""

    private lateinit var user: Profile

    private lateinit var context: Context

    // for authentication
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private fun onAuthenticationSucceeded() {
        //    TODO() Здесь нужно указать переход к следующему активити в случае успешной аутенчего-то-там

//            this@SignInActivity.finish()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigninBinding.inflate(layoutInflater)

        context = this

        setContentView(binding.root)

        initHash()

        initListeners()

        initAuth()
    }


    private fun checkAuthenticationFINGERPRINT() {
        biometricPrompt.authenticate(promptInfo)
    }

    private fun initAuth() {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    Toast.makeText(this@SignInActivity, "Успешно", Toast.LENGTH_LONG)
                        .show()

                    Log.e("Apps", "Success")

                    lifecycleScope.launch(Dispatchers.Main) {
                        paintDots(count = 4)

                        delay(800)

                        onAuthenticationSucceeded()
                    }


                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)

                    binding.passButtonFingerprint.isVisible = false
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    Log.e("Apps", "Failed")

                    Toast.makeText(this@SignInActivity, "Отмена авторизации", Toast.LENGTH_LONG)
                        .show()


                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Вход в Learn2Invest")
            .setNegativeButtonText("ОТМЕНА")
            .build()
    }


    private fun initHash() {
        lifecycleScope.launch(Dispatchers.IO) {
            user = Learn2InvestApp.mainDB.profileDao().getProfile()[0]
        }
    }

    private fun checkAuthenticationPIN() {
        if (PasswordHasher(user = user).verify(pinCode)) {
            Toast.makeText(context, "PIN true", Toast.LENGTH_SHORT).show()

            lifecycleScope.launch(Dispatchers.IO) {
                delay(1000)

                onAuthenticationSucceeded()
            }
        } else {
            Toast.makeText(context, "PIN false", Toast.LENGTH_SHORT).show()

            lifecycleScope.launch(Dispatchers.Main) {
                delay(500)

                pinCode = ""

                //рекурсия тут ок, т.к. второй раз сюда просто так не попасть
                paintDots()
            }
        }
    }

    private fun paintDots(count: Int = pinCode.length) {
        when (count) {
            1 -> {
                binding.dot1.drawable.setTint(Color.BLACK)

                binding.dot2.drawable.setTint(Color.WHITE)

                binding.dot3.drawable.setTint(Color.WHITE)

                binding.dot4.drawable.setTint(Color.WHITE)
            }

            2 -> {
                binding.dot1.drawable.setTint(Color.BLACK)

                binding.dot2.drawable.setTint(Color.BLACK)

                binding.dot3.drawable.setTint(Color.WHITE)

                binding.dot4.drawable.setTint(Color.WHITE)
            }

            3 -> {
                binding.dot1.drawable.setTint(Color.BLACK)

                binding.dot2.drawable.setTint(Color.BLACK)

                binding.dot3.drawable.setTint(Color.BLACK)

                binding.dot4.drawable.setTint(Color.WHITE)
            }

            4 -> {
                binding.dot1.drawable.setTint(Color.BLACK)

                binding.dot2.drawable.setTint(Color.BLACK)

                binding.dot3.drawable.setTint(Color.BLACK)

                binding.dot4.drawable.setTint(Color.BLACK)

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
        }

        paintDots()

        if (pinCode.length == 4) {
            checkAuthenticationPIN()

            paintDots()
        }
    }

    private fun backspace() {
        if (pinCode.isNotEmpty()) {
            pinCode = pinCode.substring(0, pinCode.lastIndex)

            paintDots(count = pinCode.length)
        }
    }

    private fun initListeners() {
        binding.apply {


            passButton0.setOnClickListener {
                updatePin("0")
            }

            passButton1.setOnClickListener {
                updatePin("1")
            }

            passButton2.setOnClickListener {
                updatePin("2")
            }

            passButton3.setOnClickListener {
                updatePin("3")
            }

            passButton4.setOnClickListener {
                updatePin("4")
            }

            passButton5.setOnClickListener {
                updatePin("5")
            }

            passButton6.setOnClickListener {
                updatePin("6")
            }

            passButton7.setOnClickListener {
                updatePin("7")
            }

            passButton8.setOnClickListener {
                updatePin("8")
            }

            passButton9.setOnClickListener {
                updatePin("9")
            }

            passButtonBackspace.setOnClickListener {
                backspace()
            }

            passButtonFingerprint.setOnClickListener {
                checkAuthenticationFINGERPRINT()
            }
        }
    }
}