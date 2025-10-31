package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.db.AppDataBase
import com.example.playlistmaker.library.data.db.TrackDbConverter
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.library.domain.FavoriteRepository
import com.example.playlistmaker.search.data.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteRepositoryImpl(
    private val appDataBase: AppDataBase,
    private val trackDbConverter: TrackDbConverter
) : FavoriteRepository {
    override suspend fun addToFavorite(t: Track) {
        appDataBase.trackDao().insertFavTrack(trackDbConverter.map(t))
    }

    override suspend fun deleteFromFavorite(t: Track) {
        appDataBase.trackDao().deleteFavTrack(trackDbConverter.map(t))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDataBase.trackDao().getFavoriteTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }
}