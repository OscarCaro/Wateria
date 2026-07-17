package com.wateria

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WateriaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Required only while legacy services can still be started by Android.
        AndroidThreeTen.init(this)
    }
}
