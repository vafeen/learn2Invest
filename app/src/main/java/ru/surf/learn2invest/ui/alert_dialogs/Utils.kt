package ru.surf.learn2invest.ui.alert_dialogs

import android.text.Editable
import ru.surf.learn2invest.app.App
import ru.surf.learn2invest.noui.cryptography.PasswordHasher

fun Editable.isTrueTradingPassword(): Boolean = PasswordHasher(
    firstName = App.profile.firstName,
    lastName = App.profile.lastName
).passwordToHash(
    this.toString()
) == App.profile.tradingPasswordHash


fun Float.getWithCurrency(): String = "$ $this"

fun String.getFloatFromStringWithCurrency(): Float = this.toFloat()