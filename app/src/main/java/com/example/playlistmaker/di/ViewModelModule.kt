package com.example.playlistmaker.di

import com.example.playlistmaker.library.ui.FavTracksViewModel
import com.example.playlistmaker.library.ui.PlaylistsViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.data.Track
import com.example.playlistmaker.search.ui.TracksSearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel<TracksSearchViewModel> {
        TracksSearchViewModel(get(), get())
    }
    viewModel<SettingsViewModel>{
        SettingsViewModel(get(),get())
    }
    viewModel<PlayerViewModel>{ (track: Track) ->
        PlayerViewModel(track,get())
    }
    viewModel<FavTracksViewModel> {
        FavTracksViewModel(get())
    }
    viewModel<PlaylistsViewModel> {
        PlaylistsViewModel()
    }
}