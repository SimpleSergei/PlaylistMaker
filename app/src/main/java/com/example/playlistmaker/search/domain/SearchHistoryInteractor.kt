package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryInteractor {
    fun getHistory(): Flow<List<Track>>
    fun saveToHistory(t: Track)
    fun clearHistory()
    }
