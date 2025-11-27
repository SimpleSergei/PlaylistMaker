package com.example.playlistmaker.search.domain


sealed interface TracksSearchState {
    object Loading : TracksSearchState
    data class Content(val track: List<Track>) : TracksSearchState
    data class Error(val errorCode: Int) : TracksSearchState
    data class Empty(val track: List<Track>) : TracksSearchState
    data class SearchHistory(val history: List<Track>) : TracksSearchState
    object SearchHistoryCleared : TracksSearchState
}