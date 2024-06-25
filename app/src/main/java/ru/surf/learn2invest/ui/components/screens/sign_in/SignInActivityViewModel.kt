package ru.surf.learn2invest.ui.components.screens.sign_in

import android.content.Context
import androidx.lifecycle.ViewModel
import ru.surf.learn2invest.noui.cryptography.FingerprintAuthenticator

class SignInActivityViewModel : ViewModel() {
    var pinCode: String = ""
    var firstPin: String = "" // Apps для сравнения во время регистрации
    var isVerified = false
    var userDataIsChanged = false
    lateinit var fingerPrintManager: FingerprintAuthenticator
    var keyBoardIsWork = true

    fun blockKeyBoard() {
        keyBoardIsWork = false
    }

    fun unBlockKeyBoard() {
        keyBoardIsWork = true
    }

}