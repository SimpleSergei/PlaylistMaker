package com.example.playlistmaker.library.domain

sealed interface PlaylistState {
    object Empty : PlaylistState
    data class Content(val playlists: List<Playlist>) : PlaylistState
}