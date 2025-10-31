package com.example.playlistmaker.di

import com.example.playlistmaker.library.domain.FavoriteInteractor
import com.example.playlistmaker.library.domain.FavoriteInteractorImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksInteractorImpl
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import org.koin.dsl.module

val domainModule = module {
    single<TracksRepository> {
        TracksRepositoryImpl(get(),get())
    }
    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }
    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }
    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }
    single<SettingsInteractor>{
        SettingsInteractorImpl(get())
    }
    single<FavoriteInteractor> {
        FavoriteInteractorImpl(get())
    }
}