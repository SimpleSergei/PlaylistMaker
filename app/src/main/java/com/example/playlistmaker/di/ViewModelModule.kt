package com.example.playlistmaker.di

import com.example.playlistmaker.library.ui.FavTracksViewModel
import com.example.playlistmaker.library.ui.PlaylistsLibraryViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.playlists.ui.PlaylistCreateViewModel
import com.example.playlistmaker.playlists.ui.PlaylistDetailsViewModel
import com.example.playlistmaker.playlists.ui.PlaylistEditViewModel
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.TracksSearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel<TracksSearchViewModel> {
        TracksSearchViewModel(get(), get())
    }
    viewModel<SettingsViewModel> {
        SettingsViewModel(get(), get())
    }
    viewModel<PlayerViewModel> { (track: Track) ->
        PlayerViewModel(track, get(),get())
    }
    viewModel<FavTracksViewModel> {
        FavTracksViewModel(get())
    }
    viewModel<PlaylistsLibraryViewModel> {
        PlaylistsLibraryViewModel(get())
    }
    viewModel<PlaylistCreateViewModel>{
        PlaylistCreateViewModel(get())
    }
    viewModel<PlaylistDetailsViewModel> { (playlistId: Long) ->
        PlaylistDetailsViewModel(playlistId, get())
    }
    viewModel<PlaylistEditViewModel> { (playlistId: Long) ->
        PlaylistEditViewModel(playlistId,get())
    }
}