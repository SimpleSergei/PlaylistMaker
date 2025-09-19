package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun addTrackToHistory(track: Track)
    fun clearTrackHistory()
    fun getSearchHistory(): ArrayList<Track>?
}