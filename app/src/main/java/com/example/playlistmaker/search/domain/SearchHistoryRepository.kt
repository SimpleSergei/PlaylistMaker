package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.data.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun saveToHistory(t: Track)
    fun getHistory(): Flow<List<Track>>
    fun clearHistory()
}