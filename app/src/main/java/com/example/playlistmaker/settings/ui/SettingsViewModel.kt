package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.SettingsEvent
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.data.SingleLiveEvent
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(private val sharingInteractor: SharingInteractor, private val settingsInteractor: SettingsInteractor) : ViewModel() {

    private val navigationEventLiveData = SingleLiveEvent<SettingsEvent>()
    fun observeNavigationEvent(): LiveData<SettingsEvent> = navigationEventLiveData

    fun shareApp() {
        val shareIntent = sharingInteractor.getShareAppIntent()
        navigationEventLiveData.value = SettingsEvent.ShareApp(shareIntent)
    }

    fun openTerms() {
        val termsIntent = sharingInteractor.getTermsIntent()
        navigationEventLiveData.value = SettingsEvent.OpenTerms(termsIntent)
            }

    fun openSupport() {
        val supportIntent = sharingInteractor.getSupportIntent()
        navigationEventLiveData.value = SettingsEvent.OpenSupport(supportIntent)
    }
    fun switchTheme(){
        settingsInteractor.updateThemeSettings()
    }
    fun getThemeSettings(): Boolean{
        return settingsInteractor.getThemeSettings()
    }
}
