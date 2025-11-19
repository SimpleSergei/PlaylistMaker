package com.example.playlistmaker.library.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun saveToFavorite(t: Track)
    suspend fun deleteFromFavorite(t: Track)
    suspend fun getFavoriteTracksId(): List<String>
}