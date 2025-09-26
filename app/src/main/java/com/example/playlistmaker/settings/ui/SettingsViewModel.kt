package com.example.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(private val sharingInteractor: SharingInteractor, private val settingsRepository: SettingsRepository) : ViewModel() {
    companion object {
        fun getFactory(sharingInteractor: SharingInteractor, settingsRepository: SettingsRepository): ViewModelProvider.Factory =
            viewModelFactory { initializer { SettingsViewModel(sharingInteractor,settingsRepository) } }
    }

    fun shareApp(){
        sharingInteractor.shareApp()
    }
    fun openTerms(){
        sharingInteractor.openTerms()
            }
    fun openSupport(){
        sharingInteractor.openSupport()
    }
    fun switchTheme(){
        settingsRepository.updateThemeSettings()
    }
    fun getThemeSettings(): Boolean{
        return settingsRepository.getThemeSettings()
    }
}
