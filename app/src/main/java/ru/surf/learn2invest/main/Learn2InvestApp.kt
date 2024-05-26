package ru.surf.learn2invest.main

import android.app.Application
import ru.surf.learn2invest.notifications.NotificationChannels
import ru.surf.learn2invest.notifications.registerNotificationChannels
import ru.surf.learn2invest.noui.database_components.Learn2InvestDatabase
import ru.surf.learn2invest.noui.database_components.entity.Profile

class Learn2InvestApp : Application() {
    companion object {
        lateinit var mainDB: Learn2InvestDatabase
            private set

        var idOfProfile = 0

        var profile: Profile? = null
    }

    override fun onCreate() {
        super.onCreate()

        mainDB = Learn2InvestDatabase.buildDatabase(context = this)

        this.registerNotificationChannels(NotificationChannels.allChannels)

    }
}