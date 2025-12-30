package com.example.famtracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class FamTracker : Application() {
    override fun onCreate() {
        super.onCreate()
        // Atur user agent dengan nama paket aplikasi Anda.
        // Ini WAJIB dilakukan sebelum menggunakan MapView.
        Configuration.getInstance().userAgentValue = packageName
    }
}