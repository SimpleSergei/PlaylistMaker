package com.example.playlistmaker.creator

import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.data.SearchHistoryInteractorImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {

    fun getSearchHistoryInterator(sharedPreferences: SharedPreferences): SearchHistoryInteractor{
        return SearchHistoryInteractorImpl(sharedPreferences = sharedPreferences)
    }
    private fun getTracksRepository(): TracksRepository{
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }
    fun provideTracksInteractor(): TracksInteractor{
        return TracksInteractorImpl(getTracksRepository())
    }
}