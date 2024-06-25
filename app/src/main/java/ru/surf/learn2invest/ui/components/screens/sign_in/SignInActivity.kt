package ru.surf.learn2invest.ui.components.screens.sign_in

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.ActivitySigninBinding
import ru.surf.learn2invest.noui.cryptography.FingerprintAuthenticator
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.cryptography.isBiometricAvailable
import ru.surf.learn2invest.noui.cryptography.verifyPIN
import ru.surf.learn2invest.noui.database_components.DatabaseRepository.profile
import ru.surf.learn2invest.ui.components.screens.host.HostActivity
import ru.surf.learn2invest.utils.gotoCenter
import ru.surf.learn2invest.utils.tapOn
import ru.surf.learn2invest.utils.updateProfile

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding
    private lateinit var viewModel: SignInActivityViewModel

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
        viewModel = ViewModelProvider(this)[SignInActivityViewModel::class.java]
        setContentView(binding.root)
        initListeners()
        paintDots()
        when (intent.action) {
            SignINActivityActions.SignIN.action -> {
                if (profile.biometry) viewModel.fingerPrintManager.auth()
            }

            SignINActivityActions.SignUP.action -> {
                binding.enterPinSignin.text =
                    ContextCompat.getString(this@SignInActivity, R.string.create_pin)

                binding.passButtonFingerprint.isVisible = false
            }

            SignINActivityActions.ChangingPIN.action -> {
                binding.enterPinSignin.text =
                    ContextCompat.getString(this@SignInActivity, R.string.enter_old_pin)

                binding.passButtonFingerprint.isVisible = false
            }
        }
    }

    private fun onAuthenticationSucceeded() {
        if (intent.action != SignINActivityActions.ChangingPIN.action)
            startActivity(Intent(this@SignInActivity, HostActivity::class.java))
        viewModel.pinCode = ""
        if (viewModel.userDataIsChanged) updateProfile(lifecycleCoroutineScope = lifecycleScope)
        this@SignInActivity.finish()
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
        if (viewModel.keyBoardIsWork) {
            if (viewModel.pinCode.length < 4) viewModel.pinCode += num
            paintDots()
            if (viewModel.pinCode.length == 4) {
                viewModel.blockKeyBoard()
                when (intent.action) {

                    SignINActivityActions.SignIN.action -> {
                        val isAuthSucceeded = verifyPIN(user = profile, viewModel.pinCode)
                        animatePINCode(isAuthSucceeded).invokeOnCompletion {
                            if (isAuthSucceeded) onAuthenticationSucceeded()
                            else viewModel.pinCode = ""
                        }
                    }

                    SignINActivityActions.SignUP.action -> {
                        when {
                            viewModel.firstPin == "" -> {
                                viewModel.firstPin = viewModel.pinCode
                                viewModel.pinCode = ""
                                lifecycleScope.launch(Dispatchers.Main) {
                                    delay(500)
                                    paintDots()
                                    binding.enterPinSignin.text = getString(R.string.repeat_pin)
                                    viewModel.unBlockKeyBoard()
                                }
                            }

                            viewModel.firstPin == viewModel.pinCode -> {
                                profile = profile.copy(
                                    hash = PasswordHasher(
                                        firstName = profile.firstName,
                                        lastName = profile.lastName
                                    ).passwordToHash(viewModel.pinCode)
                                )
                                viewModel.userDataIsChanged = true
                                animatePINCode(truth = true).invokeOnCompletion {
                                    if (isBiometricAvailable(context = this@SignInActivity)) {
                                        viewModel.fingerPrintManager.setSuccessCallback {
                                            profile = profile.copy(
                                                biometry = true
                                            )

                                            onAuthenticationSucceeded()
                                        }.setCancelCallback {
                                            onAuthenticationSucceeded()
                                        }.auth()
                                    } else {
                                        onAuthenticationSucceeded()
                                    }
                                }
                            }

                            viewModel.firstPin != viewModel.pinCode -> {
                                viewModel.pinCode = ""

                                animatePINCode(truth = false)
                            }
                        }

                    }

                    SignINActivityActions.ChangingPIN.action -> {
                        when {
                            // вводит старый пароль
                            viewModel.firstPin == "" && !viewModel.isVerified -> {
                                //если ввел верно
                                viewModel.isVerified = verifyPIN(user = profile, viewModel.pinCode)
                                viewModel.pinCode = ""
                                animatePINCode(
                                    truth = viewModel.isVerified, needReturn = true
                                ).invokeOnCompletion {
                                    if (viewModel.isVerified) binding.enterPinSignin.text =
                                        ContextCompat.getString(
                                            this@SignInActivity,
                                            R.string.enter_new_pin
                                        )
                                    paintDots()
                                    viewModel.unBlockKeyBoard()
                                }
                            }

                            //вводит новый
                            viewModel.firstPin == "" && viewModel.isVerified -> {
                                viewModel.firstPin = viewModel.pinCode
                                viewModel.pinCode = ""
                                lifecycleScope.launch(Dispatchers.Main) {
                                    delay(500)
                                    paintDots()
                                }.invokeOnCompletion {
                                    binding.enterPinSignin.text =
                                        ContextCompat.getString(
                                            this@SignInActivity,
                                            R.string.repeat_pin
                                        )
                                    viewModel.unBlockKeyBoard()
                                }

                            }

                            // повторяет
                            viewModel.firstPin != "" && viewModel.isVerified -> {
                                val truth = viewModel.pinCode == viewModel.firstPin
                                if (truth) {
                                    viewModel.userDataIsChanged = true
                                    profile = profile.copy(
                                        hash = PasswordHasher(
                                            firstName = profile.firstName,
                                            lastName = profile.lastName
                                        ).passwordToHash(viewModel.pinCode)
                                    )
                                }

                                animatePINCode(
                                    truth = truth, needReturn = true
                                ).invokeOnCompletion {
                                    viewModel.pinCode = ""
                                    if (truth) onAuthenticationSucceeded()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initListeners() {
        viewModel.fingerPrintManager = FingerprintAuthenticator(
            context = this, lifecycleCoroutineScope = lifecycleScope
        ).setSuccessCallback {
            if (intent.action == SignINActivityActions.SignUP.action) {
                profile = profile.copy(biometry = true)
                viewModel.userDataIsChanged = true
            }
            animatePINCode(truth = true).invokeOnCompletion {
                onAuthenticationSucceeded()
            }
        }.setDesignBottomSheet(
            title = ContextCompat.getString(
                this@SignInActivity,
                R.string.sign_in_in_learn2invest
            )
        )

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
                numberButtons[index].setOnClickListener { it ->
                    updatePin("$index")
                    (it as TextView).tapOn()
                }
            }

            passButtonBackspace.setOnClickListener {
                if (viewModel.pinCode.isNotEmpty()) {
                    viewModel.pinCode = viewModel.pinCode.substring(0, viewModel.pinCode.lastIndex)
                    paintDots(count = viewModel.pinCode.length)
                }
            }

            passButtonFingerprint.isVisible =
                if (isBiometricAvailable(context = this@SignInActivity)) {
                    passButtonFingerprint.setOnClickListener {
                        viewModel.fingerPrintManager.auth()
                    }
                    true
                } else false
        }
    }
}