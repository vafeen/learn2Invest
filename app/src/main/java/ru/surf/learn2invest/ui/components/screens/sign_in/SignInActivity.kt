package ru.surf.learn2invest.ui.components.screens.sign_in


import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.app.App.Companion.profile
import ru.surf.learn2invest.databinding.ActivitySigninBinding
import ru.surf.learn2invest.noui.cryptography.FingerprintAuthenticator
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.cryptography.isBiometricAvailable
import ru.surf.learn2invest.noui.cryptography.verifyPIN
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.ui.components.screens.host.HostActivity
import ru.surf.learn2invest.ui.tapOn


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

        window.statusBarColor = ContextCompat.getColor(this, R.color.main_background)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.main_background
                )
            )
        )
        window.navigationBarColor = ContextCompat.getColor(this, R.color.main_background)

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

            animatePINCode(truth = true).invokeOnCompletion {
                onAuthenticationSucceeded()
            }

        }.setDesignBottomSheet(
            title = "Вход в Learn2Invest"
        )

        when (intent.action) {

            SignINActivityActions.SignIN.action -> {
                if (profile.biometry) {
                    fingerPrintManager.auth()
                }
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

                binding.passButtonFingerprint.isVisible = false
            }

        }

    }

    private fun updateProfileData() {
        if (userDataIsChanged) {
            lifecycleScope.launch(Dispatchers.IO) {
                DatabaseRepository.updateProfile(profile)
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

                startActivityWithMainLogic()

                this@SignInActivity.finish()
            }

            SignINActivityActions.SignUP.action -> {

                startActivityWithMainLogic()

                this@SignInActivity.finish()
            }

            SignINActivityActions.ChangingPIN.action -> {

                this@SignInActivity.finish()
            }
        }

        updateProfileData()
    }


    private fun checkAuthenticationPin(): Boolean = verifyPIN(user = profile, pinCode)

    private fun animatePINCode(truth: Boolean, needReturn: Boolean = false): Job {
        return lifecycleScope.launch(Dispatchers.Main) {
            delay(100)

            binding.apply {
                dot1.gotoCenter(truePIN = truth, needReturn = needReturn)
                dot2.gotoCenter(truePIN = truth, needReturn = needReturn)
                dot3.gotoCenter(truePIN = truth, needReturn = needReturn)
                dot4.gotoCenter(truePIN = truth, needReturn = needReturn)
            }

            delay(800)
        }
    }

    private fun ImageView.gotoCenter(truePIN: Boolean, needReturn: Boolean) {
        val home = (this.layoutParams as ConstraintLayout.LayoutParams).horizontalBias

        val gotoCenter = ValueAnimator.ofFloat(
            home,
            0.5f
        ).also {
            it.duration = 300

            it.addUpdateListener { animator ->
                val biasValue = animator.animatedValue as Float

                val params = this.layoutParams as ConstraintLayout.LayoutParams

                params.horizontalBias = biasValue

                this.layoutParams = params
            }

            it.startDelay
        }

        val goPoDomam = ValueAnimator.ofFloat(
            0.5f,
            home
        ).also {
            it.duration = 300

            it.addUpdateListener { animator ->
                val biasValue = animator.animatedValue as Float

                val params = this.layoutParams as ConstraintLayout.LayoutParams

                params.horizontalBias = biasValue

                this.layoutParams = params
            }
        }

        goPoDomam.doOnEnd {
            unBlockKeyBoard()
        }

        gotoCenter.start()

        gotoCenter.doOnEnd {

            lifecycleScope.launch(Dispatchers.Main) {
                this@gotoCenter.drawable.setTint(
                    if (truePIN) {
                        Color.GREEN
                    } else {
                        Color.RED
                    }
                )

                delay(800)

                if (needReturn || !truePIN) {
                    goPoDomam.doOnStart {
                        this@gotoCenter.drawable.setTint(Color.WHITE)
                    }

                    goPoDomam.start()
                }
            }

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

    private fun paintDots(count: Int = pinCode.length) {
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

    private fun blockKeyBoard() {
        keyBoardIsWork = false
    }

    private fun unBlockKeyBoard() {
        keyBoardIsWork = true
    }

    private fun updatePin(num: String) {
        if (keyBoardIsWork) {
            if (pinCode.length < 4) {
                pinCode += num
            }

            paintDots()

            if (pinCode.length == 4) {
                when (intent.action) {

                    SignINActivityActions.SignIN.action -> {

                        blockKeyBoard()

                        val isAuthSucceeded = checkAuthenticationPin()

                        animatePINCode(isAuthSucceeded).invokeOnCompletion {

                            if (isAuthSucceeded) {
                                onAuthenticationSucceeded()
                            }

                        }

                        pinCode = ""
                    }

                    SignINActivityActions.SignUP.action -> {
                        blockKeyBoard()

                        when {
                            firstPin == "" -> {

                                firstPin = pinCode

                                pinCode = ""

                                lifecycleScope.launch(Dispatchers.Main) {

                                    delay(500)

                                    paintDots()

                                    binding.enterPinSignin.text = getString(R.string.repeat_pin)

                                    unBlockKeyBoard()
                                }
                            }

                            firstPin == pinCode -> {


                                profile = profile.copy(
                                    hash = PasswordHasher(
                                        firstName = profile.firstName,
                                        lastName = profile.lastName
                                    ).passwordToHash(pinCode)
                                )

                                userDataIsChanged = true

                                animatePINCode(truth = true).invokeOnCompletion {

                                    if (isBiometricAvailable(context = context)) {
                                        fingerPrintManager.setSuccessCallback {
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

                            firstPin != pinCode -> {

                                pinCode = ""

                                animatePINCode(truth = false)
                            }
                        }


                    }

                    SignINActivityActions.ChangingPIN.action -> {
                        blockKeyBoard()

                        when {
                            // вводит старый пароль
                            firstPin == "" && !isVerified -> {

                                //если ввел верно
                                isVerified = checkAuthenticationPin()

                                pinCode = ""

                                animatePINCode(
                                    truth = isVerified,
                                    needReturn = true
                                ).invokeOnCompletion {

                                    if (isVerified) {
                                        binding.enterPinSignin.text = "Введите новый пинкод"
                                    }

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
                                    binding.enterPinSignin.text = "Повторите пинкод"

                                    unBlockKeyBoard()
                                }

                            }

                            // повторяет
                            firstPin != "" && isVerified -> {
                                val truth = pinCode == firstPin

                                if (truth) {

                                    userDataIsChanged = true

                                    profile = profile.copy(
                                        hash = PasswordHasher(
                                            firstName = profile.firstName,
                                            lastName = profile.lastName
                                        ).passwordToHash(pinCode)
                                    )
                                }

                                animatePINCode(
                                    truth = truth,
                                    needReturn = true
                                ).invokeOnCompletion {
                                    pinCode = ""

                                    if (truth) {
                                        onAuthenticationSucceeded()
                                    }
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

                numberButtons[index].setOnClickListener { it ->
                    updatePin("$index")

                    numberButtons[index].tapOn()
                }
            }

            passButtonBackspace.setOnClickListener {
                backspace()
            }

            passButtonFingerprint.isVisible = if (isBiometricAvailable(context = context)) {
                passButtonFingerprint.setOnClickListener {
                    fingerPrintManager.auth()
                }

                true
            } else {
                false
            }
        }
    }

}