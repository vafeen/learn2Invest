package ru.surf.learn2invest.notifications

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


fun Context.registerNotificationChannel(channel: NotificationChannels) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            channel.ID,
            channel.NAME,
            channel.IMPORTANCE
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }

}

/**
 * Функция запроса разрешения на отправку уведомлений.
 * Её необходимо добавить в активити "Мои инвестиции"
 */
fun requestPermissionForNotifications(context: Context) {
    val permissionState =
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)

    // If the permission is not granted, request it.
    if (permissionState == PackageManager.PERMISSION_DENIED) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            1
        )
    }
}

