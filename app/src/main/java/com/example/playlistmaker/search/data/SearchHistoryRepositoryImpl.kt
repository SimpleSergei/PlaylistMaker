package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.SearchHistoryRepository

const val MAX_COUNT_TRACKS = 10

class SearchHistoryRepositoryImpl(private val storage: StorageClient<ArrayList<Track>>):
    SearchHistoryRepository {
    override fun saveToHistory(t: Track) {
        val currentTracks = storage.getData() ?: arrayListOf()
        currentTracks.removeAll { newTrack ->
            newTrack.trackId == t.trackId
        }
        currentTracks.add(0, t)
        if (currentTracks.size > MAX_COUNT_TRACKS) {
            currentTracks.removeAt(currentTracks.size - 1)
        }
        storage.storageData(currentTracks)
    }

    override fun getHistory(): Resource<List<Track>> {
        val tracks = storage.getData()?:listOf()
        return Resource.Success(tracks)
    }

    override fun clearHistory() {
        storage.deleteData()
    }
}