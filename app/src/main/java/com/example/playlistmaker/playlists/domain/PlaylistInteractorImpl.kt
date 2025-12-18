package com.example.playlistmaker.playlists.domain

import android.content.Context
import android.net.Uri
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {
    override suspend fun createPlaylist(name: String, description: String, coverPath: String) {
        playlistRepository.createPlaylist(name, description, coverPath)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistRepository.deletePlaylist(playlistId)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun addToPlaylistTracksTable(t: Track) {
        playlistRepository.addToPlaylistTracksTable(t)
    }

    override suspend fun addToPlaylist(t: Track, p: Playlist) {
        playlistRepository.addToPlaylist(t,p)
    }

    override fun copyImageToInternalStorage(context: Context, uri: Uri): Uri {
        return playlistRepository.copyImageToInternalStorage(context, uri)
    }

    override suspend fun getPlaylistById(id: Long): Playlist {
        return playlistRepository.getPlaylistById(id)
    }

    override suspend fun getTracksByIds(playlistId: Long): List<Track> {
        return playlistRepository.getTracksByIds(playlistId)
    }

    override suspend fun deleteTrackById(playlistId: Long, trackId: String) {
        return playlistRepository.deleteTrackById(playlistId,trackId)
    }

    override suspend fun deleteTrackFromDBById(trackId: String) {
        return playlistRepository.deleteTrackFromDBById(trackId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        return playlistRepository.updatePlaylist(playlist)
    }
}