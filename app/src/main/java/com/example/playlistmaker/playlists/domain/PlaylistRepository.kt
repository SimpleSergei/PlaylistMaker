package com.example.playlistmaker.playlists.domain

import android.content.Context
import android.net.Uri
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow


interface PlaylistRepository {
    suspend fun createPlaylist(name: String, description: String, coverPath: String)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addToPlaylistTracksTable(t: Track)
    suspend fun addToPlaylist(t:Track, p: Playlist)
    fun copyImageToInternalStorage(context: Context, uri: Uri): Uri
    suspend fun getPlaylistById(id:Long): Playlist
    suspend fun getTracksByIds(playlistId: Long): List<Track>
    suspend fun deleteTrackById(playlistId:Long, trackId: String)
    suspend fun deleteTrackFromDBById(trackId: String)
    suspend fun updatePlaylist(playlist: Playlist)
}