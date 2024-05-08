package ru.surf.learn2invest.main

import android.app.Application
import androidx.room.Room
import ru.surf.learn2invest.database_components.Learn2InvestDatabase

class Learn2InvestApp : Application() {
    companion object {
        lateinit var mainDB: Learn2InvestDatabase
            private set

    }

    override fun onCreate() {
        super.onCreate()
        mainDB = Room
            .databaseBuilder(
                this,
                Learn2InvestDatabase::class.java,
                Learn2InvestDatabase.NAME
            )
            .build()

    }
}