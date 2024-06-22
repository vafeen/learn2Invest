package ru.surf.learn2invest.ui.alert_dialogs

import android.text.Editable
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.database_components.DatabaseRepository

fun Editable.isTrueTradingPassword(): Boolean = PasswordHasher(
    firstName = DatabaseRepository.profile.firstName,
    lastName = DatabaseRepository.profile.lastName
).passwordToHash(
    this.toString()
) == DatabaseRepository.profile.tradingPasswordHash


fun Float.getWithCurrency(): String = "$this$"

fun String.getFloatFromStringWithCurrency(): Float {
    val result = this.substring(0, this.lastIndex).toFloat()

    return result
}
