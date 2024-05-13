package ru.surf.learn2invest.notifications

import android.app.Notification
import android.app.NotificationManager
import android.content.Context

/**
 * "Сервис" для отправки уведомлений.
 *
 * Использование:
 * ```
 *
 *         NotificationService(context = this).showNotification(
 *             Notifications.createPriceAlertNotification(
 *                 context = this,
 *                 title = "title",
 *                 contentText = "contentText",
 *                 // выбранный приоритет из NotificationCompat
 *                 priority = NotificationCompat.PRIORITY_DEFAULT
 *             )
 *         )
 *
 * // или
 *
 *         NotificationService(context = this)
 *                      .cancelNotification(id = 0)
 *```
 *
 */

class NotificationService(context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }

    fun showNotification(notification: Notification) {
        notificationManager.notify(0, notification)
    }
}

