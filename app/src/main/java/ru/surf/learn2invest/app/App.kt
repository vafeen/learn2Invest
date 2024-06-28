package ru.surf.learn2invest.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.decode.SvgDecoder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import ru.surf.learn2invest.noui.database_components.entity.Profile
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var databaseRepository: DatabaseRepository

    companion object {
        lateinit var imageLoader: ImageLoader
    }

    override fun onCreate() {
        super.onCreate()
        imageLoader = ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
        with(ProcessLifecycleOwner.get()) {
            lifecycleScope.launch(Dispatchers.IO) {
                Log.d("db", "database repository App= $databaseRepository")
                databaseRepository.getAllAsFlowProfile().collect { profList ->
                    if (profList.isNotEmpty()) {
                        databaseRepository.profile = profList[databaseRepository.idOfProfile]
                    } else {
                        databaseRepository.profile = Profile(
                            id = 0,
                            firstName = "undefined",
                            lastName = "undefined",
                            biometry = false,
                            fiatBalance = 0f,
                            assetBalance = 0f
                        )
                        databaseRepository.insertAllProfile(databaseRepository.profile)
                    }
                }
            }
        }
    }
}