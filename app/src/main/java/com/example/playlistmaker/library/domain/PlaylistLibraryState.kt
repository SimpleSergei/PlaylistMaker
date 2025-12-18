package com.example.playlistmaker.library.domain

import com.example.playlistmaker.playlists.domain.Playlist

sealed interface PlaylistLibraryState {
    object Empty : PlaylistLibraryState
    data class Content(val playlists: List<Playlist>) : PlaylistLibraryState
}