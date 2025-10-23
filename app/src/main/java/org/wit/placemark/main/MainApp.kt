package org.wit.placemark.main

import android.app.Application
import org.wit.placemark.models.CafeMemStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    val cafes = CafeMemStore() // renamed from placemarks

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("Cafe tracker started")
    }
}
