package ru.surf.learn2invest.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.surf.learn2invest.noui.cryptography.PasswordHasher
import ru.surf.learn2invest.noui.database_components.L2IDatabase
import ru.surf.learn2invest.noui.database_components.entity.Profile

class App : Application() {
    companion object {
        lateinit var mainDB: L2IDatabase
            private set

        var idOfProfile = 0

        lateinit var profile: Profile
    }

    override fun onCreate() {
        super.onCreate()


        mainDB = L2IDatabase.buildDatabase(context = this)

        val profileFlow: Flow<List<Profile>> = mainDB.profileDao().getAllAsFlow()

        with(ProcessLifecycleOwner.get()) {
            lifecycleScope.launch(Dispatchers.IO) {
                profileFlow.collect { profList ->
                    if (profList.isNotEmpty()) {
                        profile = profList[idOfProfile]
                    } else {
                        profile = Profile(
                            id = 0,
                            firstName = "undefined",
                            lastName = "undefined",
                            biometry = false,
                            fiatBalance = 0,
                            assetBalance = 0,
                            hash = PasswordHasher(
                                firstName = "undefined",
                                lastName = "undefined"
                            ).passwordToHash("0000"),
//                            tradingPasswordHash = PasswordHasher(
//                                firstName = "undefined",
//                                lastName = "undefined"
//                            ).passwordToHash("1235789")
                        )
                    }
                    Log.d("profile", "profile = $profile ")
                }
            }
        }
    }
}