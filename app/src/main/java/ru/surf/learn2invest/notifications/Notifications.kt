package ru.surf.learn2invest.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import ru.surf.learn2invest.R
import ru.surf.learn2invest.main.MainActivity


/**
 * Класс для удобного создания уведомлений.
 *
 * Использование:
 * ```
 * Notifications.createPriceAlertNotification(
 *                 context = this,
 *                 title = "title",
 *                 contentText = "contentText",
 *                 priority = NotificationCompat.PRIORITY_DEFAULT
 *             )
 * ```
 */
class Notifications {

    private fun createPendingIntent(context: Context): PendingIntent {
        val myIntent = Intent(context, MainActivity::class.java)

        return PendingIntent.getActivity(
            context, 0,
            myIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {

        fun createPriceAlertNotification(
            context: Context,
            title: String,
            contentText: String,
            /**
             * NotificationCompat.<...>
             */
            priority: Int,
        ): Notification {

            val pendingIntent = Notifications().createPendingIntent(context = context)

            return NotificationCompat.Builder(
                context,
                NotificationChannels.PriceAlert.ID
            )
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(contentText)
                .setPriority(priority)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()


        }


    }
}