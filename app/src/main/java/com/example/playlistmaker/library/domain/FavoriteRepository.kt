package com.example.playlistmaker.library.domain

import com.example.playlistmaker.search.data.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun addToFavorite(t: Track)
    suspend fun deleteFromFavorite(t: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
}