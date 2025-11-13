package com.example.playlistmaker.library.domain

import com.example.playlistmaker.search.data.Track

sealed interface FavoriteTracksState {
    object Empty: FavoriteTracksState
    data class Content(val tracks: List<Track>) : FavoriteTracksState
}