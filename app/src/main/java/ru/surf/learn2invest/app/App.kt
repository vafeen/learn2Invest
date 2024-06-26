package ru.surf.learn2invest.app

import android.app.Application
import coil.ImageLoader
import coil.decode.SvgDecoder
import ru.surf.learn2invest.noui.database_components.DatabaseRepository

class App : Application() {
    companion object {
        lateinit var imageLoader: ImageLoader
    }

    override fun onCreate() {
        super.onCreate()
        DatabaseRepository.initDatabase(context = this)
        imageLoader = ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }
}