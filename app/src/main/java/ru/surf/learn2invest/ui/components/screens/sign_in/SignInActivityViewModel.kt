package ru.surf.learn2invest.ui.components.screens.sign_in

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.noui.cryptography.FingerprintAuthenticator
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.ui.components.screens.host.HostActivity
import javax.inject.Inject

@HiltViewModel
class SignInActivityViewModel @Inject constructor(
    var databaseRepository: DatabaseRepository,
    var fingerprintAuthenticator: FingerprintAuthenticator
) :
    ViewModel() {
    var pinCode: String = ""
    var firstPin: String = ""
    var isVerified = false
    var userDataIsChanged = false
    var keyBoardIsWork = true


    fun blockKeyBoard() {
        keyBoardIsWork = false
    }

    fun unBlockKeyBoard() {
        keyBoardIsWork = true
    }

    fun onAuthenticationSucceeded(
        action: String,
        context: Activity
    ) {
        if (action != SignINActivityActions.ChangingPIN.action)
            context.startActivity(Intent(context, HostActivity::class.java))
        pinCode = ""
        if (userDataIsChanged) updateProfile()
        context.finish()
    }

    private fun updateProfile() =
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.updateProfile(databaseRepository.profile)
        }
}