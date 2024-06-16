package ru.surf.learn2invest.ui.alert_dialogs

import android.text.Editable
import android.util.Log
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.noui.cryptography.PasswordHasher

fun Editable.isTrueTradingPassword(): Boolean = PasswordHasher(
    firstName = App.profile.firstName,
    lastName = App.profile.lastName
).passwordToHash(
    this.toString()
) == App.profile.tradingPasswordHash


fun Float.getWithCurrency(): String = "$this$"

fun String.getFloatFromStringWithCurrency(): Float {
    val result = this.substring(0, this.lastIndex).toFloat()

    Log.d("float", "result = $result")

    return result
}
