package ru.netology.mapsmarkers.application

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import ru.netology.mapsmarkers.BuildConfig

private const val MAP_API_KEY = BuildConfig.MAP_API_KEY

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(MAP_API_KEY)
    }
}