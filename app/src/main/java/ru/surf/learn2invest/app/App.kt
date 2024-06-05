package ru.surf.learn2invest.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.surf.learn2invest.notifications.NotificationChannels
import ru.surf.learn2invest.notifications.registerNotificationChannels
import ru.surf.learn2invest.noui.database_components.L2IDatabase
import ru.surf.learn2invest.noui.database_components.entity.Profile

class App : Application() {
    companion object {
        lateinit var mainDB: L2IDatabase
            private set

        var idOfProfile = 0

        lateinit var profileFlow: Flow<List<Profile>>

        var profile: Profile? = null
    }

    override fun onCreate() {
        super.onCreate()

        mainDB = L2IDatabase.buildDatabase(context = this)

        profileFlow = mainDB.profileDao().getAllAsFlow()

        with(ProcessLifecycleOwner.get()) {
            lifecycleScope.launch(Dispatchers.IO) {
                profileFlow.collect {
                    if (it.isNotEmpty()) {
                        profile = it[idOfProfile]
                    }
                }
            }
        }

        this.registerNotificationChannels(NotificationChannels.allChannels)

    }
}