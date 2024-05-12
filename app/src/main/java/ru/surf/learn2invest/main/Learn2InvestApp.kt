package ru.surf.learn2invest.main

import android.app.Application
import ru.surf.learn2invest.noui.database_components.Learn2InvestDatabase

class Learn2InvestApp : Application() {
    companion object {
        lateinit var mainDB: Learn2InvestDatabase
            private set

    }

    override fun onCreate() {
        super.onCreate()

        mainDB = Learn2InvestDatabase.buildDatabase(context = this)

    }
}