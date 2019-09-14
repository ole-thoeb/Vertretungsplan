package com.example.eloem.vertretungsplan

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import org.jetbrains.anko.defaultSharedPreferences

class CustomApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
    
        val themeMode = when(val theme = defaultSharedPreferences.getString("settingsTheme", "2")) {
            "0" -> AppCompatDelegate.MODE_NIGHT_NO
            "1" -> AppCompatDelegate.MODE_NIGHT_YES
            "2" -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            else -> throw Error("Unknown theme option: $theme")
        }
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }
}