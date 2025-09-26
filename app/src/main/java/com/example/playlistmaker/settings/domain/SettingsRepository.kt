package com.example.playlistmaker.settings.domain

interface SettingsRepository {
    fun getThemeSettings(): Boolean
    fun updateThemeSettings()
}