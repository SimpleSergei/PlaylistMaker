package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.data.Resource
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
}