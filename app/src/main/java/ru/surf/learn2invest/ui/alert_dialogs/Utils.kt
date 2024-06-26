package ru.surf.learn2invest.ui.alert_dialogs


fun Float.getWithCurrency(): String = "$this$"

fun String.getFloatFromStringWithCurrency(): Float = this.substring(0, this.lastIndex).toFloat()