package ru.surf.learn2invest.noui.cryptography

import android.app.Activity
import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
class FingerprintAuthenticator(
    private val context: Activity,
    val lifecycleCoroutineScope: LifecycleCoroutineScope
) {

    fun setSuccessCallback(function: () -> Unit): FingerprintAuthenticator {
        this.successCallBack = function

        return this
    }

    fun setFailedCallback(function: () -> Unit): FingerprintAuthenticator {
        this.failedCallBack = function

        return this
    }

    fun setCancelCallback(function: () -> Unit): FingerprintAuthenticator {
        this.setCancelCallback = function

        return this
    }

    fun setDesignBottomSheet(
        title: String,
        cancelText: String = "ОТМЕНА"
    ): FingerprintAuthenticator {
        titleText = title
        cancelButtonText = cancelText
        return this
    }

    fun auth(): Job {
        return lifecycleCoroutineScope.launch(Dispatchers.Main) {
            if (isBiometricAvailable(context = context)) {
                initFingerPrintAuth()
                checkAuthenticationFingerprint()
            }
        }
    }

    // callbacks
    private var failedCallBack: () -> Unit = {}
    private var successCallBack: () -> Unit = {}
    private var setCancelCallback: () -> Unit = {}

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
                        setCancelCallback()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        failedCallBack()
                    }
                })

        promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle(titleText)
            .setNegativeButtonText(cancelButtonText).build()

        return this
    }

}