package ru.surf.learn2invest.ui.components.screens.sign_in


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.R
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.app.App.Companion.profile
import ru.surf.learn2invest.databinding.ActivitySigninBinding
import ru.surf.learn2invest.noui.cryptography.FingerprintAuthenticator
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.cryptography.verifyPIN
import ru.surf.learn2invest.ui.components.screens.host.HostActivity


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding

    private var pinCode: String = ""
    private var firstPin: String = "" // Apps для сравнения во время регистрации
    private var isVerified = false

    private var userDataIsChanged = false

    private lateinit var context: Context

    private lateinit var fingerPrintManager: FingerprintAuthenticator

    private var keyBoardIsWork = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigninBinding.inflate(layoutInflater)

        context = this

        setContentView(binding.root)

        initListeners()

        paintDots()

        fingerPrintManager = FingerprintAuthenticator(
            context = this, lifecycleCoroutineScope = lifecycleScope
        ).setSuccessCallback {
            if (intent.action == SignINActivityActions.SignUP.action) {
                profile = profile.copy(biometry = true)

                userDataIsChanged = true
            }

            onAuthenticationSucceeded()
        }.setDesignBottomSheet(
            title = "Вход в Learn2Invest"
        )

        when (intent.action) {

            SignINActivityActions.SignIN.action -> {

                fingerPrintManager.auth()
            }

            SignINActivityActions.SignUP.action -> {

                binding.enterPinSignin.text = buildString {
                    append("Придумайте PIN-код") // Просто, чтобы не захламлять strings.xml :)
                }

                binding.passButtonFingerprint.isVisible = false
            }

            SignINActivityActions.ChangingPIN.action -> {

                binding.enterPinSignin.text = buildString {
                    append("Введите старый PIN-код") // Просто, чтобы не захламлять strings.xml :)
                }

            }

        }

    }

    private fun startActivityWithMainLogic() {
        val intent = Intent(context, HostActivity::class.java)
        startActivity(intent)
        pinCode = ""
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

//                fingerPrintManager.auth()
//                    .invokeOnCompletion {

                startActivityWithMainLogic()

//                lifecycleScope.launch(Dispatchers.IO) {
//                updateProfileData()
//                }

                //Loher.d("Activity stop")

                this@SignInActivity.finish()
//                }
            }

            SignINActivityActions.ChangingPIN.action -> {
//                updateProfileData()

                this@SignInActivity.finish()
            }
        }


    }


    private fun checkAuthenticationPin(): Boolean = verifyPIN(user = profile, pinCode)

    private suspend fun showErrorPINCode() {

        delay(300)

        paintDots(count = -1)

        delay(300)

        paintDots()
    }

    private suspend fun showTruePINCode() {

        delay(300)

//        pinCode = ""

//        paintDots()

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

    private fun blockKeyBoard() {
        Log.d("block", "blocked")
        keyBoardIsWork = false
    }

    private fun unBlockKeyBoard() {
        Log.d("block", "un blocked")
        keyBoardIsWork = true
    }

    private fun updatePin(num: String) {

        if (pinCode.length < 4 && keyBoardIsWork) {
            pinCode += num
        } else {
            Toast.makeText(context, "не тыкай с#ка", Toast.LENGTH_SHORT).show()
        }

        Log.d("pin", "last = $firstPin")
        Log.d("pin", "pin = $pinCode")

        paintDots()

        if (pinCode.length == 4) {
            when (intent.action) {

                SignINActivityActions.SignIN.action -> {
                    if (checkAuthenticationPin()) {

                        blockKeyBoard()

                        lifecycleScope.launch(Dispatchers.Main) {
                            showTruePINCode()
                        }.invokeOnCompletion {

                            onAuthenticationSucceeded()
                        }

                    } else {
                        pinCode = ""

                        blockKeyBoard()

                        lifecycleScope.launch(Dispatchers.Main) {

                            showErrorPINCode()

                        }.invokeOnCompletion {

                            unBlockKeyBoard()

                        }

                    }
                }

                SignINActivityActions.SignUP.action -> {
                    when {
                        firstPin == "" -> {
                            firstPin = pinCode

                            pinCode = ""

                            blockKeyBoard()

                            lifecycleScope.launch(Dispatchers.Main) {
                                delay(500)

                                paintDots()

                                binding.enterPinSignin.text = getString(R.string.repeat_pin)
                            }.invokeOnCompletion {

                                unBlockKeyBoard()

                            }
                        }

                        firstPin == pinCode -> {
                            //Loher.d("$pinCode == $firstPin")
                            //Loher.d("user = $user")


                            blockKeyBoard()

                            lifecycleScope.launch(Dispatchers.Main) {
                                showTruePINCode()

                                App.mainDB.profileDao().update(
                                    profile.copy(
                                        hash = PasswordHasher(
                                            firstName = profile.firstName,
                                            lastName = profile.lastName
                                        ).passwordToHash(pinCode)
                                    )
                                )

                                fingerPrintManager.auth()

                                onAuthenticationSucceeded()
                            }.invokeOnCompletion {

                                unBlockKeyBoard()

                            }

                        }

                        firstPin != pinCode -> {

                            pinCode = ""

                            blockKeyBoard()

                            lifecycleScope.launch(Dispatchers.Main) {
                                showErrorPINCode()
                            }.invokeOnCompletion {

                                unBlockKeyBoard()

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

                                blockKeyBoard()

                                lifecycleScope.launch(Dispatchers.Main) {
                                    showTruePINCode()
                                }.invokeOnCompletion {
                                    paintDots()

                                    binding.enterPinSignin.text = "Введите новый пинкод"

                                    unBlockKeyBoard()
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

                                profile = profile.copy(
                                    hash = PasswordHasher(
                                        firstName = profile.firstName, lastName = profile.lastName
                                    ).passwordToHash(pinCode)
                                )

                                userDataIsChanged = true

                                lifecycleScope.launch(Dispatchers.Main) {
                                    withContext(Dispatchers.IO) {
                                        App.mainDB.profileDao().update(profile)
                                    }

                                    showTruePINCode()

                                }.invokeOnCompletion {
                                    pinCode = ""

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
                fingerPrintManager.auth()
            }
        }
    }

}