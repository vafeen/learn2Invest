package ru.surf.learn2invest.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.decode.SvgDecoder
import dagger.hilt.android.HiltAndroidApp
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
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
        imageLoader = ImageLoader.Builder(this).components {
            add(SvgDecoder.Factory())
        }.build()
        with(ProcessLifecycleOwner.get()) {
            databaseRepository.enableProfileFlow(lifecycleCoroutineScope = lifecycleScope)
        }
    }
}