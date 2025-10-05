package com.example.playlistmaker.settings.domain

import android.content.Intent

sealed class SettingsEvent {
    data class ShareApp(val intent: Intent) : SettingsEvent()
    data class OpenSupport(val intent: Intent) : SettingsEvent()
    data class OpenTerms(val intent: Intent) : SettingsEvent()
}