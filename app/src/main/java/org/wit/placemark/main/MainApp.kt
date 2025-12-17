package org.wit.placemark.main

import android.app.Application
import org.wit.placemark.models.*
import timber.log.Timber

class MainApp : Application() {

    lateinit var cafes: CafeStore
    lateinit var users: UserStore   // 👈 REQUIRED

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        cafes = CafeJsonStore(applicationContext)
        users = UserJsonStore(applicationContext) // 👈 REQUIRED
    }
}
