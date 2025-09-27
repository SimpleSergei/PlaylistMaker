package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator

const val DARK_THEME_KEY = "dark_theme"
const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"

class App : Application() {
    companion object {
        lateinit var appContext: Context
    }
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        val settingsInteractor = Creator.provideSettingsInteractor()
        var darkTheme = settingsInteractor.getThemeSettings()
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
