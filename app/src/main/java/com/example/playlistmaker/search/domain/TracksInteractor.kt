package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {

    fun searchTracks(expression: String): Flow<Pair<List<Track>?, Int?>>
}