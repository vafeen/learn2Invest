package ru.surf.learn2invest.app

import android.app.Application
import ru.surf.learn2invest.noui.database_components.DatabaseRepository

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        DatabaseRepository.initDatabase(context = this)

    }
}