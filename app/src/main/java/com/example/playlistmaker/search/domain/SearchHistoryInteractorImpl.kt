package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository):
    SearchHistoryInteractor {

    override fun getHistory(): Flow<List<Track>> {
        return repository.getHistory()
    }

    override fun saveToHistory(t: Track) {
        repository.saveToHistory(t)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}