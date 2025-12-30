package com.example.famtracker

import android.app.Application
import androidx.preference.PreferenceManager // Pastikan menggunakan androidx
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class FamTracker : Application() {
    override fun onCreate() {
        super.onCreate()

        val ctx = applicationContext
        // Tambahkan .getDefaultSharedPreferences(ctx) di akhir
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)

        Configuration.getInstance().load(ctx, prefs)
        Configuration.getInstance().userAgentValue = packageName
    }
}