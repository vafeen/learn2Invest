package ru.surf.learn2invest.app

import android.app.Application
import ru.surf.learn2invest.notifications.NotificationChannels
import ru.surf.learn2invest.notifications.registerNotificationChannels
import ru.surf.learn2invest.noui.database_components.L2IDatabase
import ru.surf.learn2invest.noui.database_components.entity.Profile

class App : Application() {
    companion object {
        lateinit var mainDB: L2IDatabase
            private set

        var idOfProfile = 0

        var profile: Profile? = null
    }

    override fun onCreate() {
        super.onCreate()

        mainDB = L2IDatabase.buildDatabase(context = this)

        this.registerNotificationChannels(NotificationChannels.allChannels)

    }
}