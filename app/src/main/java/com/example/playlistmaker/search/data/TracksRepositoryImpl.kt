package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(-1))
            }

            200 -> {
                val tracksResponse = response as TracksSearchResponse
                val data = tracksResponse.results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate ?: "Unknown",
                        it.primaryGenreName,
                        it.country, it.previewUrl ?: "Unknown"
                    )
                }
                emit(Resource.Success(data))
            }

            else -> {
                emit(Resource.Error(response.resultCode))
            }

        }
    }
}