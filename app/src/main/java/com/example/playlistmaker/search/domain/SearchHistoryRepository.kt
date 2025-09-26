package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.data.Resource
import com.example.playlistmaker.search.data.Track

interface SearchHistoryRepository {
    fun saveToHistory(t: Track)
    fun getHistory(): Resource<List<Track>>
    fun clearHistory()
}