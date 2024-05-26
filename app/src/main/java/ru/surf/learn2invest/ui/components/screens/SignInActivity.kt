package ru.surf.learn2invest.ui.components.screens


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.ActivitySigninBinding
import ru.surf.learn2invest.main.Learn2InvestApp
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.database_components.entity.Profile
import ru.surf.learn2invest.noui.logs.Loher
import java.util.concurrent.Executor


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding


    private var pinCode: String = ""
    private var firstPin: String = "" // Apps для сравнения во время регистрации
    private var isVerified = false

    private var userDataIsChanged = false

    private lateinit var user: Profile

    private lateinit var context: Context

    // for authentication
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigninBinding.inflate(layoutInflater)


        context = this

        setContentView(binding.root)

        initListeners()

        when (intent.action) {

            SignINActivityActions.SignIN.action -> {
                initProfile()

                initFingerPrintAuth()

//                пин: ввели, полсекунды, вход
//                отпечаток: 4 штуки закрасили, полсекунды, вход

            }

            SignINActivityActions.SignUP.action -> {
                initProfile()

                initFingerPrintAuth()

                binding.enterPinSignin.text = buildString {
                    append("Придумайте PIN-код") // Просто, чтобы не захламлять strings.xml :)
                }


                binding.passButtonFingerprint.isVisible = false
                //                ввели пин,
//                открыли отпечаток
//                        прикоснулись
//                вход
            }

            SignINActivityActions.ChangingPIN.action -> {
//                ввели новый пин,
//                стерли
//                повторили пин
//                        если ок, вошли
                binding.enterPinSignin.text = buildString {
                    append("Введите старый PIN-код") // Просто, чтобы не захламлять strings.xml :)
                }

                initProfile()


            }

        }

    }

    private fun startActivityWithMainLogic() {
        //    TODO() Надя,Здесь нужно указать переход к активити "Мои инвестиции" в случае успешной аутен-чего-то-там и входа

    }


    private fun onAuthenticationSucceeded() {
        when (intent.action) {

            SignINActivityActions.SignIN.action -> {
                if (pinCode.length < 4) {
                    paintDots(count = 4)
                }

                startActivityWithMainLogic()

                this@SignInActivity.finish()
            }

            SignINActivityActions.SignUP.action -> {
                startActivityWithMainLogic()

                lifecycleScope.launch(Dispatchers.IO) {
                    updateProfileData()
                }

                Loher.d("Activity stop")

                this@SignInActivity.finish()
            }

            SignINActivityActions.ChangingPIN.action -> {
                updateProfileData()

                this@SignInActivity.finish()
            }
        }


    }

    private fun updateProfileData() {
        if (userDataIsChanged) {

            lifecycleScope.launch(Dispatchers.IO) {
                Learn2InvestApp.mainDB.profileDao().insertAll(user)
            }

        }
    }

    private fun checkAuthenticationFingerprint() {
        biometricPrompt.authenticate(promptInfo)
    }

    private fun initFingerPrintAuth() {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    Loher.d("Success")

                    if (intent.action == SignINActivityActions.SignUP.action) {
                        user = user.copy(biometry = true)

                        userDataIsChanged = true
                    }

                    onAuthenticationSucceeded()

                }

                override fun onAuthenticationError(
                    errorCode: Int, errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    Loher.e("Authentication failed")
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle("Вход в Learn2Invest")
            .setNegativeButtonText("ОТМЕНА").build()
    }


    private fun initProfile() {
        lifecycleScope.launch(Dispatchers.IO) {
            val list = Learn2InvestApp.mainDB.profileDao().getProfile()

            if (list.isNotEmpty()) {
                user = list[0]

                Loher.d("user = $user")
            } else {
                Loher.e("user not found")
            }
        }
    }

    private fun checkAuthenticationPin(): Boolean = PasswordHasher(user = user).verify(pinCode)

    private suspend fun showErrorPINCode() {

        delay(300)

        paintDots(count = -1)

        delay(300)

        paintDots()
    }

    private suspend fun showTruePINCode() {

        delay(300)

//        paintDots(count = 100)

//        delay(300)
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

            // error
            -1 -> {
                binding.dot1.drawable.setTint(Color.RED)

                binding.dot2.drawable.setTint(Color.RED)

                binding.dot3.drawable.setTint(Color.RED)

                binding.dot4.drawable.setTint(Color.RED)
            }

            //true
//            100 -> {
//                binding.dot1.drawable.setTint(Color.GREEN)
//
//                binding.dot2.drawable.setTint(Color.GREEN)
//
//                binding.dot3.drawable.setTint(Color.GREEN)
//
//                binding.dot4.drawable.setTint(Color.GREEN)
//            }

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
            when (intent.action) {

                SignINActivityActions.SignIN.action -> {
                    if (checkAuthenticationPin()) {

                        lifecycleScope.launch(Dispatchers.Main) {
                            showTruePINCode()
                        }.invokeOnCompletion {
                            onAuthenticationSucceeded()
                        }

                    } else {
                        pinCode = ""
                        lifecycleScope.launch(Dispatchers.Main) {
                            showErrorPINCode()
                        }

                    }
                }

                SignINActivityActions.SignUP.action -> {
                    when {
                        firstPin == "" -> {
                            firstPin = pinCode

                            lifecycleScope.launch(Dispatchers.Main) {
                                delay(500)

                                Loher.d("pin = $pinCode")
                                Loher.d("fpin = $firstPin")

                                pinCode = ""

                                paintDots()

                                binding.enterPinSignin.text = getString(R.string.repeat_pin)
                            }
                        }

                        firstPin == pinCode -> {
                            Loher.d("$pinCode == $firstPin")
                            Loher.d("user = $user")

                            user = user.let {
                                it.copy(hash = PasswordHasher(user = it).passwordToHash(pinCode))
                            }

                            lifecycleScope.launch(Dispatchers.Main) {
                                showTruePINCode()

                                checkAuthenticationFingerprint()

                                onAuthenticationSucceeded()
                            }

                        }

                        firstPin != pinCode -> {

                            pinCode = ""

                            lifecycleScope.launch(Dispatchers.Main) {
                                showErrorPINCode()
                            }
                        }
                    }


                }

                SignINActivityActions.ChangingPIN.action -> {
                    when {
                        // вводит старый пароль
                        firstPin == "" && !isVerified -> {
                            //если ввел верно
                            if (checkAuthenticationPin()) {
                                isVerified = true

                                pinCode = ""

                                lifecycleScope.launch(Dispatchers.Main) {
                                    showTruePINCode()
                                }.invokeOnCompletion {
                                    paintDots()

                                    binding.enterPinSignin.text = "Введите новый пинкод"
                                }

                            } else {
                                pinCode = ""

                                lifecycleScope.launch(Dispatchers.Main) {
                                    showErrorPINCode()
                                }
                            }
                        }

                        //вводит новый
                        firstPin == "" && isVerified -> {

                            firstPin = pinCode

                            pinCode = ""

                            lifecycleScope.launch(Dispatchers.Main) {
                                delay(500)

                                paintDots()
                            }.invokeOnCompletion {
                                binding.enterPinSignin.text = "Повторите пинкод"
                            }

                        }

                        // повторяет
                        firstPin != "" && isVerified -> {
                            if (pinCode == firstPin) {
                                user = user.let {
                                    it.copy(hash = PasswordHasher(user = it).passwordToHash(pinCode))
                                }

                                userDataIsChanged = true

                                pinCode = ""

                                lifecycleScope.launch(Dispatchers.Main) {
                                    showTruePINCode()
                                }.invokeOnCompletion {
                                    onAuthenticationSucceeded()
                                }

                            } else {
                                pinCode = ""

                                lifecycleScope.launch(Dispatchers.Main) {
                                    showErrorPINCode()
                                }
                            }
                        }
                    }
                }
            }
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
            val numberButtons = listOf(
                passButton0,
                passButton1,
                passButton2,
                passButton3,
                passButton4,
                passButton5,
                passButton6,
                passButton7,
                passButton8,
                passButton9,
            )

            for (index in 0..numberButtons.lastIndex) {
                numberButtons[index].setOnClickListener {
                    updatePin("$index")
                }
            }

            passButtonBackspace.setOnClickListener {
                backspace()
            }

            passButtonFingerprint.setOnClickListener {
                checkAuthenticationFingerprint()
            }
        }
    }
}