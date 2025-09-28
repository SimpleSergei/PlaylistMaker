package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.SearchHistoryRepository

class SearchHistoryRepositoryImpl(private val storage: StorageClient<ArrayList<Track>>):
    SearchHistoryRepository {
    override fun saveToHistory(t: Track) {
        val tracks = storage.getData()?:arrayListOf()
        tracks.add(t)
        storage.storageData(tracks)
    }

    override fun getHistory(): Resource<List<Track>> {
        val tracks = storage.getData()?:listOf()
        return Resource.Success(tracks)
    }

    override fun clearHistory() {
        storage.deleteData()
    }
}