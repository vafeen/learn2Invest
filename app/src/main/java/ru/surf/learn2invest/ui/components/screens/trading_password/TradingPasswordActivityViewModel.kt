package ru.surf.learn2invest.ui.components.screens.trading_password

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.R
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import javax.inject.Inject

@HiltViewModel
class TradingPasswordActivityViewModel @Inject constructor() : ViewModel() {
    lateinit var action: TradingPasswordActivityActions
    lateinit var actionName: String
    lateinit var mainButtonAction: String

    @Inject
    lateinit var databaseRepository: DatabaseRepository
    fun saveTradingPassword(password: String) {
        databaseRepository.apply {
            profile = if (action == TradingPasswordActivityActions.CreateTradingPassword ||
                action == TradingPasswordActivityActions.ChangeTradingPassword
            ) {
                profile.copy(
                    tradingPasswordHash = PasswordHasher(
                        firstName = profile.firstName,
                        lastName = profile.lastName
                    ).passwordToHash(
                        password = password
                    )
                )
            } else profile.copy(tradingPasswordHash = null)
        }
    }

    fun updateProfile() =
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.updateProfile(databaseRepository.profile)
        }

    fun initAction(intentAction: String, context: Context): Boolean {
        var isOk = true
        action = when (intentAction) {
            TradingPasswordActivityActions.ChangeTradingPassword.action -> {
                apply {
                    actionName = ContextCompat.getString(
                        context,
                        R.string.change_trading_password
                    )
                    mainButtonAction =
                        ContextCompat.getString(context, R.string.change)
                }
                TradingPasswordActivityActions.ChangeTradingPassword
            }

            TradingPasswordActivityActions.RemoveTradingPassword.action -> {
                apply {
                    actionName = ContextCompat.getString(
                        context,
                        R.string.remove_trading_password
                    )
                    mainButtonAction =
                        ContextCompat.getString(context, R.string.remove)
                }
                TradingPasswordActivityActions.RemoveTradingPassword
            }

            TradingPasswordActivityActions.CreateTradingPassword.action -> {
                actionName = ContextCompat.getString(
                    context,
                    R.string.create_trading_password
                )
                mainButtonAction =
                    ContextCompat.getString(context, R.string.create)

                isOk = intentAction == TradingPasswordActivityActions.CreateTradingPassword.action

                TradingPasswordActivityActions.CreateTradingPassword
            }

            else -> {
                // finish if action is not defined

                TradingPasswordActivityActions.CreateTradingPassword
            }
        }
        return isOk
    }
}