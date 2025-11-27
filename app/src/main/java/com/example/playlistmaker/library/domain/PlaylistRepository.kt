package com.example.playlistmaker.library.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow


interface PlaylistRepository {
    suspend fun createPlaylist(p: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun deletePlaylist(p: Playlist)
    suspend fun addToPlaylistTracksTable(t: Track)
    suspend fun addToPlaylist(t:Track, p: Playlist)
}