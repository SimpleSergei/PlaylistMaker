package com.example.playlistmaker.library.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {
    override suspend fun createPlaylist(p: Playlist) {
        playlistRepository.createPlaylist(p)
    }

    override suspend fun deletePlaylist(p: Playlist) {
        playlistRepository.deletePlaylist(p)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun addToPlaylistTracksTable(t: Track) {
        playlistRepository.addToPlaylistTracksTable(t)
    }

    override suspend fun addToPlaylist(
        t: Track,
        p: Playlist
    ) {
        playlistRepository.addToPlaylist(t,p)
    }
}