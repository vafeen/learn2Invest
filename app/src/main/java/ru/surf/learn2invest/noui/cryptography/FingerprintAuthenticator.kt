package ru.surf.learn2invest.noui.cryptography

import android.app.Activity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.utils.isBiometricAvailable
import java.util.concurrent.Executor

/**
 * Аутентификация пользователя с помощью отпечатка пальца
 * @param context [контекст аутентификации (Activity)]
 * @param lifecycleCoroutineScope [scope для выполнения аутентификации]
 */
class FingerprintAuthenticator(
    private val context: Activity,
    val lifecycleCoroutineScope: LifecycleCoroutineScope
) {
    /**
     * Сеттер для callback в случае успешной аутентифиакции
     * @param function [callback в случае успешной аутентификации]
     */
    fun setSuccessCallback(function: () -> Unit): FingerprintAuthenticator {
        this.successCallBack = function

        return this
    }

    /**
     * Сеттер для callback в случае неуспешной аутентифиакции
     * @param function [callback ]
     */
    fun setFailedCallback(function: () -> Unit): FingerprintAuthenticator {
        this.failedCallBack = function

        return this
    }

    /**
     * Сеттер для callback в случае отмены пользователем аутентифиакции
     * @param function [callback ]
     */
    fun setCancelCallback(function: () -> Unit): FingerprintAuthenticator {
        this.setCancelCallback = function

        return this
    }

    /**
     * Настройка дизайна названия BottomSheet и кнопки отмены действия
     * @param title [название BottomSheet]
     * @param cancelText [Текст кнопки отмены]
     */
    fun setDesignBottomSheet(
        title: String,
        cancelText: String = ContextCompat.getString(context, R.string.cancel)
    ): FingerprintAuthenticator {
        titleText = title
        cancelButtonText = cancelText
        return this
    }

    /**
     * Показ BottomSheet для аутентификации
     */
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
    private var titleText: String = ContextCompat.getString(context, R.string.example_title)
    private var cancelButtonText: String = ContextCompat.getString(context, R.string.cancel)

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