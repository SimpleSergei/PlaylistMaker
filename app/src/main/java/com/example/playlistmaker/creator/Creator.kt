package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.App
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.Track
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.storage.PrefsStorageClient
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksInteractorImpl
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import com.google.gson.reflect.TypeToken

object Creator {
    private val appContext: Context get()= App.appContext

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            PrefsStorageClient<ArrayList<Track>>(
                appContext,
                "SEARCH_HISTORY",
                object :
                    TypeToken<ArrayList<Track>> (){}.type))
    }
    fun provideSearchHistoryInteractor(): SearchHistoryInteractor{
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }
    private fun getTracksRepository(): TracksRepository{
        return TracksRepositoryImpl(RetrofitNetworkClient(appContext))
    }
    fun provideTracksInteractor(): TracksInteractor{
        return TracksInteractorImpl(getTracksRepository())
    }
    fun provideSharingInteractor(): SharingInteractor{
        return SharingInteractorImpl(getExternalNavigator(),appContext)
    }
    private fun getExternalNavigator(): ExternalNavigator{
        return ExternalNavigator(appContext)
    }
    fun provideSettingsInteractor(): SettingsInteractor{
        return SettingsInteractorImpl(getSettingsRepository())
    }
    private fun getSettingsRepository(): SettingsRepository{
        return SettingsRepositoryImpl(appContext)
    }
}