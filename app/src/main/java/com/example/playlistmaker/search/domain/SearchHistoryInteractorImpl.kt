package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.data.Track

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository):
    SearchHistoryInteractor {
    override fun getHistory(consumer: SearchHistoryInteractor.HistoryConsumer) {
        consumer.consume(repository.getHistory().data)
    }

    override fun saveToHistory(t: Track) {
        repository.saveToHistory(t)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}