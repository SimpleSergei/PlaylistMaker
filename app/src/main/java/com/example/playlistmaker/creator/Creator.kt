package com.example.playlistmaker.creator

import android.content.Context
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
import com.example.playlistmaker.settings.domain.SettingsRepositoryImpl
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import com.google.gson.reflect.TypeToken

object Creator {

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            PrefsStorageClient<ArrayList<Track>>(
                context,
                "SEARCH_HISTORY",
                object :
                    TypeToken<ArrayList<Track>> (){}.type))
    }
    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor{
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }
    private fun getTracksRepository(context: Context): TracksRepository{
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }
    fun provideTracksInteractor(context: Context): TracksInteractor{
        return TracksInteractorImpl(getTracksRepository(context))
    }
    fun provideSharingInteractor(context: Context): SharingInteractor{
        return SharingInteractorImpl(getExternalNavigator(context),context)
    }
    private fun getExternalNavigator(context: Context): ExternalNavigator{
        return ExternalNavigator(context)
    }
    fun provideSettingsRepository(context:Context): SettingsRepository{
        return SettingsRepositoryImpl(context)
    }
}