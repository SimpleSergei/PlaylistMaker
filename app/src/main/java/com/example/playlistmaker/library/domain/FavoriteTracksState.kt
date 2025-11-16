package com.example.playlistmaker.library.domain

import com.example.playlistmaker.search.domain.Track

sealed interface FavoriteTracksState {
    object Empty: FavoriteTracksState
    data class Content(val tracks: List<Track>) : FavoriteTracksState
}