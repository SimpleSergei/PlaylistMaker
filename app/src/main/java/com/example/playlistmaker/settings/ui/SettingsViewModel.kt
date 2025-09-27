package com.example.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(private val sharingInteractor: SharingInteractor, private val settingsInteractor: SettingsInteractor) : ViewModel() {
    companion object {
        fun getFactory(sharingInteractor: SharingInteractor, settingsInteractor: SettingsInteractor): ViewModelProvider.Factory =
            viewModelFactory { initializer { SettingsViewModel(sharingInteractor,settingsInteractor) } }
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
        settingsInteractor.updateThemeSettings()
    }
    fun getThemeSettings(): Boolean{
        return settingsInteractor.getThemeSettings()
    }
}
