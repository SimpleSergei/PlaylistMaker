package com.example.playlistmaker.playlists.domain

import com.example.playlistmaker.search.domain.Track

sealed interface PlaylistDetailsState {
    data class Empty(
        val playlist: Playlist,
        val totalDuration: Long = 0L
    ) : PlaylistDetailsState

    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>,
        val totalDuration: Long
    ) : PlaylistDetailsState

    object Deleted : PlaylistDetailsState
}