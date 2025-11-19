package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun saveToHistory(t: Track)
    fun getHistory(): Flow<List<Track>>
    fun clearHistory()
}