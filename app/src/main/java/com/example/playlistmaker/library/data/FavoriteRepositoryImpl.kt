package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.db.TrackDbConverter
import com.example.playlistmaker.library.data.db.dao.TrackDao
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.library.domain.FavoriteRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val trackDao: TrackDao,
    private val trackDbConverter: TrackDbConverter
) : FavoriteRepository {
    override suspend fun addToFavorite(t: Track) {
        trackDao.insertFavTrack(trackDbConverter.map(t))
    }

    override suspend fun deleteFromFavorite(t: Track) {
        trackDao.deleteFavTrack(trackDbConverter.map(t))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return trackDao.getFavoriteTracks().map { tracksEntity ->
            convertFromTrackEntity(tracksEntity.reversed())
        }
    }

    override suspend fun getFavoriteTracksId(): List<String> {
        return trackDao.getFavoriteTracksId()
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track).copy(isFavorite = true) }
    }
}