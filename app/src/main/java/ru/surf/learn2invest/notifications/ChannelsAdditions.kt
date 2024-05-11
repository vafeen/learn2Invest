package ru.surf.learn2invest.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

fun Context.registerNotificationChannel(channel: NotificationChannels) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            channel.NOTIFICATION_CHANNEL_ID,
            channel.NOTIFICATION_CHANNEL_NAME,
            channel.IMPORTANCE
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }

}
