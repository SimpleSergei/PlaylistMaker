package com.example.playlistmaker.settings.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(private val sharingInteractor: SharingInteractor, private val settingsInteractor: SettingsInteractor) : ViewModel() {

    fun shareApp(context: Context){
        sharingInteractor.shareApp(context)
    }
    fun openTerms(context: Context){
        sharingInteractor.openTerms(context)
            }
    fun openSupport(context: Context){
        sharingInteractor.openSupport(context)
    }
    fun switchTheme(){
        settingsInteractor.updateThemeSettings()
    }
    fun getThemeSettings(): Boolean{
        return settingsInteractor.getThemeSettings()
    }
}
