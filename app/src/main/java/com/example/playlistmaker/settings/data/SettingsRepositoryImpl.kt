package com.example.playlistmaker.settings.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.DARK_THEME_KEY
import com.example.playlistmaker.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.settings.domain.SettingsRepository
import androidx.core.content.edit

class SettingsRepositoryImpl(context: Context) : SettingsRepository {
    val sharedPrefs = context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    override fun getThemeSettings(): Boolean {
        return sharedPrefs.getBoolean(DARK_THEME_KEY, false)
    }

    override fun updateThemeSettings() {
        val isDarkTheme = sharedPrefs.getBoolean(DARK_THEME_KEY, false)
        val newDarkThemeState = !isDarkTheme

        val newMode = if (newDarkThemeState) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        sharedPrefs.edit { putBoolean(DARK_THEME_KEY, newDarkThemeState) }
        AppCompatDelegate.setDefaultNightMode(newMode)
    }
}