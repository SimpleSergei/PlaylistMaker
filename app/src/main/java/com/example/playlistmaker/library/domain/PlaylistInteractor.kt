package com.example.playlistmaker.library.domain

import android.content.Context
import android.net.Uri
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String, coverPath: String)
    suspend fun deletePlaylist(p: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addToPlaylistTracksTable(t: Track)
    suspend fun addToPlaylist(t:Track, p: Playlist)
    fun copyImageToInternalStorage(context: Context, uri: Uri): Uri
}