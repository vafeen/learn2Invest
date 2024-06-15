package ru.surf.learn2invest.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.Profile

class App : Application() {
	companion object {
		var idOfProfile = 0

		lateinit var profile: Profile
	}

	override fun onCreate() {
		super.onCreate()

		DatabaseRepository.initDatabase(context = this)

	}
}