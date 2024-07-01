package ru.surf.learn2invest.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.HiltAndroidApp
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var databaseRepository: DatabaseRepository

    override fun onCreate() {
        super.onCreate()
        with(ProcessLifecycleOwner.get()) {
            databaseRepository.enableProfileFlow(lifecycleCoroutineScope = lifecycleScope)
        }
    }
}