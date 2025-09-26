package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.data.Track

interface SearchHistoryInteractor {
    fun getHistory(consumer: HistoryConsumer)
    fun saveToHistory (t: Track)
    fun clearHistory()
    interface HistoryConsumer{
        fun consume(searchHistory: List<Track>?)
    }
}