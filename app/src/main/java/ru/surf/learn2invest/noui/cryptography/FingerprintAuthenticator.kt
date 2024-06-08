package ru.surf.learn2invest.noui.cryptography

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

/**
 * Класс для аутен<чего-то там> пользователя с помощью отпечатка пальца.
 *
 * Пример использования:
 * ```
 * private lateinit var fingerPrintManager: FingerprintAuthenticator
 *
 * onCreate(){
 *
 * fingerPrintManager = FingerprintAuthenticator(context = this).setFailedCallback { // не обязательно
 *
 *         }.setSuccessCallback {// не обязательно
 *             if(intent.action == SignINActivityActions.SignUP.action){
 *                 user = user.copy(biometry = true)
 *
 *                 userDataIsChanged = true
 *             }
 *
 *             onAuthenticationSucceeded()
 *         }.setDesignBottomSheet( // не обязательно
 *             title = "Вход в Learn2Invest"
 *         )
 * }
 *
 *          // момент аутен<...>
 *
 *               fingerPrintManager.auth()
 *
 * ```
 */
class FingerprintAuthenticator(private val context: Activity) {

    fun setSuccessCallback(function: () -> Unit): FingerprintAuthenticator {
        this.successCallBack = function

        return this
    }

    fun setFailedCallback(function: () -> Unit): FingerprintAuthenticator {
        this.failedCallBack = function

        return this
    }

    fun setDesignBottomSheet(
        title: String,
        cancelText: String = "ОТМЕНА"
    ): FingerprintAuthenticator {
//        promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle()
//            .setNegativeButtonText("ОТМЕНА").build()
//
        titleText = title

        cancelButtonText = cancelText

        return this
    }

    fun auth() {
        initFingerPrintAuth()

        checkAuthenticationFingerprint()
    }

    // callbacks
    private var failedCallBack: () -> Unit = {}
    private var successCallBack: () -> Unit = {}

    // design bottom sheet
    private var titleText: String = "Example title"
    private var cancelButtonText: String = "CANCEL"

    // for authentication
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo


    private fun checkAuthenticationFingerprint() {
        biometricPrompt.authenticate(promptInfo)
    }

    private fun initFingerPrintAuth(): FingerprintAuthenticator {
        executor = ContextCompat.getMainExecutor(context)

        biometricPrompt =
            BiometricPrompt(
                context as FragmentActivity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)

                        successCallBack()

                    }

                    override fun onAuthenticationError(
                        errorCode: Int, errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)

                        Log.d("finger", "error")
                        Toast.makeText(
                            context,
                            "На устройстве выключена биометрия",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()

                        Log.d("finger", "failed")

                        Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()

                        failedCallBack()
                    }
                })

        promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle(titleText)
            .setNegativeButtonText(cancelButtonText).build()

        return this
    }

}