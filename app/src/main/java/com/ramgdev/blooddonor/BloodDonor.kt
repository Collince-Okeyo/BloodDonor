package com.ramgdev.blooddonor

import android.app.Application
import timber.log.Timber

class BloodDonor: Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }
}