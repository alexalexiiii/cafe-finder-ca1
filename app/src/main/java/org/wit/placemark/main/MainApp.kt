package org.wit.placemark.main

import android.app.Application
import org.wit.placemark.models.CafeStore
import org.wit.placemark.models.CafeJsonStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    // declare it as interface type, and 'lateinit'
    lateinit var cafes: CafeStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        // instantiate JSON store with context
        cafes = CafeJsonStore(applicationContext)

        i("Café tracker started with JSONStore")
    }
}
