package com.example.playlistmaker.search.data

import com.example.playlistmaker.library.data.db.AppDataBase
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

const val MAX_COUNT_TRACKS = 10

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Track>>,
    private val appDataBase: AppDataBase
) :
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

    override fun getHistory(): Flow<List<Track>> {
        return appDataBase.trackDao().getFavoriteTracks().map { favoriteTracksEntities ->
            val tracks = storage.getData() ?: emptyList()
            val favoriteTrackIds = favoriteTracksEntities.map { it.trackId }
            tracks.map { track ->
                track.copy(isFavorite = favoriteTrackIds.contains(track.trackId))
            }
        }
    }

    override fun clearHistory() {
        storage.deleteData()
    }
}