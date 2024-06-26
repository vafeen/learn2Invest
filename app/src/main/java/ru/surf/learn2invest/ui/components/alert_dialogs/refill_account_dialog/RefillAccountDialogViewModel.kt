package ru.surf.learn2invest.ui.components.alert_dialogs.refill_account_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.noui.database_components.DatabaseRepository

class RefillAccountDialogViewModel : ViewModel() {
    var enteredBalanceF: Float = 0f

    fun refill() {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseRepository.profile.also {
                DatabaseRepository.updateProfile(
                    it.copy(
                        fiatBalance = it.fiatBalance + enteredBalanceF
                    )
                )
            }
        }
    }
}