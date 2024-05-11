package ru.surf.learn2invest.notifications

import android.app.NotificationManager

enum class NotificationChannels(
    val NOTIFICATION_CHANNEL_ID: String,
    val NOTIFICATION_CHANNEL_NAME: String,
    val IMPORTANCE: Int
) {
    PriceAlert(
        NOTIFICATION_CHANNEL_ID = "channelForAlerts",
        NOTIFICATION_CHANNEL_NAME = "PriceAlert",
        IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
    )
}