package ru.surf.learn2invest.notifications

import android.app.NotificationManager

enum class NotificationChannels(
    val ID: String,
    val NAME: String,
    val IMPORTANCE: Int
) {
    PriceAlert(
        ID = "channelForAlerts",
        NAME = "PriceAlert",
        IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
    )
}