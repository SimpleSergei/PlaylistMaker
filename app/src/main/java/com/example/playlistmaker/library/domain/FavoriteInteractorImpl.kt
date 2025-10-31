package com.example.playlistmaker.library.domain

import com.example.playlistmaker.search.data.Track
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(private val favoriteRepository: FavoriteRepository) :
    FavoriteInteractor {
    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteRepository.getFavoriteTracks()
    }

    override suspend fun saveToFavorite(t: Track) {
        favoriteRepository.addToFavorite(t)
    }

    override suspend fun deleteFromFavorite(t: Track) {
        favoriteRepository.deleteFromFavorite(t)
    }
}