package ru.surf.learn2invest.ui.components.screens.sign_in

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.ActivitySigninBinding
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.utils.gotoCenter
import ru.surf.learn2invest.utils.isBiometricAvailable
import ru.surf.learn2invest.utils.tapOn
import ru.surf.learn2invest.utils.verifyPIN

/**
 * Активити ввода PIN-кода.
 *
 * Функции:
 * - Создание PIN-кода
 * - Смена PIN-кода
 * - Аутентификация пользователя по PIN-коду
 *
 * Определение функция с помощью intent.action и [SignINActivityActions][ru.surf.learn2invest.ui.components.screens.sign_in.SignINActivityActions]
 */

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding
    private val viewModel: SignInActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.main_background)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.main_background
                )
            )
        )
        window.navigationBarColor =
            ContextCompat.getColor(this, R.color.main_background)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.databaseRepository.apply {
            lifecycleScope.launch(Dispatchers.Main) {
                getAllAsFlowProfile().collect {
                    profile = it[idOfProfile]
                }
            }
        }
        initListeners()
        paintDots()
        when (intent.action) {
            SignINActivityActions.SignIN.action -> {
                if (viewModel.databaseRepository.profile.biometry) {
                    viewModel.fingerprintAuthenticator.auth(
                        lifecycleCoroutineScope = lifecycleScope,
                        activity = this@SignInActivity
                    )
                }
            }

            SignINActivityActions.SignUP.action -> {
                binding.enterPin.text =
                    ContextCompat.getString(this@SignInActivity, R.string.create_pin)

                binding.fingerprint.isVisible = false
            }

            SignINActivityActions.ChangingPIN.action -> {
                binding.enterPin.text =
                    ContextCompat.getString(this@SignInActivity, R.string.enter_old_pin)

                binding.fingerprint.isVisible = false
            }
        }
    }


    private fun animatePINCode(truth: Boolean, needReturn: Boolean = false): Job {
        return lifecycleScope.launch(Dispatchers.Main) {
            delay(100)

            binding.apply {
                dot1.gotoCenter(
                    truePIN = truth,
                    needReturn = needReturn,
                    lifecycleScope = lifecycleScope,
                    doAfter = { viewModel.unBlockKeyBoard() }
                )
                dot2.gotoCenter(
                    truePIN = truth, needReturn = needReturn, lifecycleScope = lifecycleScope
                )
                dot3.gotoCenter(
                    truePIN = truth, needReturn = needReturn, lifecycleScope = lifecycleScope
                )
                dot4.gotoCenter(
                    truePIN = truth, needReturn = needReturn, lifecycleScope = lifecycleScope
                )
            }
            delay(800)
        }
    }


    private fun changeColorOfFourDots(
        color1: Int,
        color2: Int,
        color3: Int,
        color4: Int,
    ) {
        binding.dot1.drawable.setTint(color1)
        binding.dot2.drawable.setTint(color2)
        binding.dot3.drawable.setTint(color3)
        binding.dot4.drawable.setTint(color4)
    }

    private fun paintDots(count: Int = viewModel.pinCode.length) {
        when (count) {
            1 -> {
                changeColorOfFourDots(
                    color1 = Color.BLACK,
                    color2 = Color.WHITE,
                    color3 = Color.WHITE,
                    color4 = Color.WHITE,
                )
            }

            2 -> {
                changeColorOfFourDots(
                    color1 = Color.BLACK,
                    color2 = Color.BLACK,
                    color3 = Color.WHITE,
                    color4 = Color.WHITE,
                )
            }

            3 -> {
                changeColorOfFourDots(
                    color1 = Color.BLACK,
                    color2 = Color.BLACK,
                    color3 = Color.BLACK,
                    color4 = Color.WHITE,
                )
            }

            4 -> {
                changeColorOfFourDots(
                    color1 = Color.BLACK,
                    color2 = Color.BLACK,
                    color3 = Color.BLACK,
                    color4 = Color.BLACK,
                )

            }

            // error
            -1 -> {
                changeColorOfFourDots(
                    color1 = Color.RED,
                    color2 = Color.RED,
                    color3 = Color.RED,
                    color4 = Color.RED,
                )
            }

            else -> {
                changeColorOfFourDots(
                    color1 = Color.WHITE,
                    color2 = Color.WHITE,
                    color3 = Color.WHITE,
                    color4 = Color.WHITE
                )
            }
        }
    }


    private fun updatePin(num: String) {
        viewModel.apply {
            if (keyBoardIsWork) {
                if (pinCode.length < 4) pinCode += num
                paintDots()
                if (pinCode.length == 4) {
                    blockKeyBoard()
                    when (intent.action) {

                        SignINActivityActions.SignIN.action -> {
                            val isAuthSucceeded = verifyPIN(
                                user = databaseRepository.profile,
                                pinCode
                            )
                            animatePINCode(isAuthSucceeded).invokeOnCompletion {
                                if (isAuthSucceeded) onAuthenticationSucceeded(
                                    action = intent.action ?: "",
                                    context = this@SignInActivity,
                                    lifecycleCoroutineScope = lifecycleScope
                                )
                                else pinCode = ""
                            }
                        }

                        SignINActivityActions.SignUP.action -> {
                            when {
                                firstPin == "" -> {
                                    firstPin = pinCode
                                    pinCode = ""
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        delay(500)
                                        paintDots()
                                        binding.enterPin.text = getString(R.string.repeat_pin)
                                        unBlockKeyBoard()
                                    }
                                }

                                firstPin == pinCode -> {
                                    databaseRepository.profile =
                                        databaseRepository.profile.copy(
                                            hash = PasswordHasher(
                                                firstName = databaseRepository.profile.firstName,
                                                lastName = databaseRepository.profile.lastName
                                            ).passwordToHash(pinCode)
                                        )
                                    userDataIsChanged = true
                                    animatePINCode(truth = true).invokeOnCompletion {
                                        if (isBiometricAvailable(activity = this@SignInActivity)) {
                                            viewModel.fingerprintAuthenticator.setSuccessCallback {
                                                databaseRepository.profile =
                                                    databaseRepository.profile.copy(
                                                        biometry = true
                                                    )

                                                onAuthenticationSucceeded(
                                                    action = intent.action ?: "",
                                                    context = this@SignInActivity,
                                                    lifecycleCoroutineScope = lifecycleScope
                                                )
                                            }.setCancelCallback {
                                                onAuthenticationSucceeded(
                                                    action = intent.action ?: "",
                                                    context = this@SignInActivity,
                                                    lifecycleCoroutineScope = lifecycleScope
                                                )
                                            }.auth(
                                                lifecycleCoroutineScope = lifecycleScope,
                                                activity = this@SignInActivity
                                            )
                                        } else {
                                            onAuthenticationSucceeded(
                                                action = intent.action ?: "",
                                                context = this@SignInActivity,
                                                lifecycleCoroutineScope = lifecycleScope
                                            )
                                        }
                                    }
                                }

                                firstPin != pinCode -> {
                                    pinCode = ""

                                    animatePINCode(truth = false)
                                }
                            }

                        }

                        SignINActivityActions.ChangingPIN.action -> {
                            when {
                                // вводит старый пароль
                                firstPin == "" && !isVerified -> {
                                    //если ввел верно
                                    isVerified =
                                        verifyPIN(user = databaseRepository.profile, pinCode)
                                    pinCode = ""
                                    animatePINCode(
                                        truth = isVerified, needReturn = true
                                    ).invokeOnCompletion {
                                        if (isVerified) binding.enterPin.text =
                                            ContextCompat.getString(
                                                this@SignInActivity,
                                                R.string.enter_new_pin
                                            )
                                        paintDots()
                                        unBlockKeyBoard()
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
                                        binding.enterPin.text =
                                            ContextCompat.getString(
                                                this@SignInActivity,
                                                R.string.repeat_pin
                                            )
                                        unBlockKeyBoard()
                                    }

                                }

                                // повторяет
                                firstPin != "" && isVerified -> {
                                    val truth = pinCode == firstPin
                                    if (truth) {
                                        viewModel.userDataIsChanged = true
                                        databaseRepository.profile =
                                            databaseRepository.profile.copy(
                                                hash = PasswordHasher(
                                                    firstName = databaseRepository.profile.firstName,
                                                    lastName = databaseRepository.profile.lastName
                                                ).passwordToHash(viewModel.pinCode)
                                            )
                                    }

                                    animatePINCode(
                                        truth = truth, needReturn = true
                                    ).invokeOnCompletion {
                                        pinCode = ""
                                        if (truth) onAuthenticationSucceeded(
                                            action = intent.action ?: "",
                                            context = this@SignInActivity,
                                            lifecycleCoroutineScope = lifecycleScope
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initListeners() {
        viewModel.apply {
            fingerprintAuthenticator.setSuccessCallback {
                if (intent.action == SignINActivityActions.SignUP.action) {
                    databaseRepository.profile = databaseRepository.profile.copy(biometry = true)
                    userDataIsChanged = true
                }
                animatePINCode(truth = true).invokeOnCompletion {
                    onAuthenticationSucceeded(
                        action = intent.action ?: "",
                        context = this@SignInActivity,
                        lifecycleCoroutineScope = lifecycleScope
                    )
                }
            }.setDesignBottomSheet(
                title = ContextCompat.getString(
                    this@SignInActivity,
                    R.string.sign_in_in_learn2invest
                ),
                cancelText = ContextCompat.getString(this@SignInActivity, R.string.cancel)
            )

            binding.apply {
                val numberButtons = listOf(
                    button0,
                    button1,
                    button2,
                    button3,
                    button4,
                    button5,
                    button6,
                    button7,
                    button8,
                    button9,
                )

                for (index in 0..numberButtons.lastIndex) {
                    numberButtons[index].setOnClickListener {
                        updatePin("$index")
                        (it as TextView).tapOn()
                    }
                }

                backspace.setOnClickListener {
                    if (pinCode.isNotEmpty()) {
                        pinCode =
                            pinCode.substring(0, pinCode.lastIndex)
                        paintDots(count = pinCode.length)
                    }
                }

                fingerprint.isVisible =
                    if (isBiometricAvailable(activity = this@SignInActivity) && databaseRepository.profile.biometry) {
                        fingerprint.setOnClickListener {
                            viewModel.fingerprintAuthenticator.auth(
                                lifecycleCoroutineScope = lifecycleScope,
                                activity = this@SignInActivity
                            )
                        }
                        true
                    } else false
            }
        }
    }
}