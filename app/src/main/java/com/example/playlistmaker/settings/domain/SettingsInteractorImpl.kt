package com.example.playlistmaker.settings.domain


class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun getThemeSettings(): Boolean {
        return repository.getThemeSettings()
    }

    override fun updateThemeSettings() {
        repository.updateThemeSettings()
    }
}