package ru.surf.learn2invest.main

import android.app.Application
import androidx.room.Room
import ru.surf.learn2invest.database_components.Database

class App : Application() {
    companion object {
     lateinit var mainDB: Database
            private set

    }

    override fun onCreate() {
        super.onCreate()

        mainDB = Room
            .databaseBuilder(
                this,
                Database::class.java,
                Database.NAME
            )
            .build()

    }
}