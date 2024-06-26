package ru.surf.learn2invest.utils

fun Float.round() =
    "\\S*\\.0*[0-9]{2}".toRegex().find(this.toBigDecimal().toPlainString())?.value

