package ru.surf.learn2invest.app

import android.app.Application
import ru.surf.learn2invest.notifications.NotificationChannels
import ru.surf.learn2invest.notifications.registerNotificationChannels
import ru.surf.learn2invest.noui.database_components.Learn2InvestDatabase

class Learn2InvestApp : Application() {
    companion object {
        lateinit var mainDB: Learn2InvestDatabase
            private set

    }

    override fun onCreate() {
        super.onCreate()

        mainDB = Learn2InvestDatabase.buildDatabase(context = this)

        this.registerNotificationChannels(NotificationChannels.allChannels)

    }

}