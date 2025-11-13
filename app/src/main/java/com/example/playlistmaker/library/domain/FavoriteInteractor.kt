package com.example.playlistmaker.library.domain

import com.example.playlistmaker.search.data.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun saveToFavorite(t: Track)
    suspend fun deleteFromFavorite(t: Track)
}