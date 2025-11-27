package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.db.PlaylistDbConverter
import com.example.playlistmaker.library.data.db.dao.PlaylistDao
import com.example.playlistmaker.library.domain.Playlist
import com.example.playlistmaker.library.domain.PlaylistRepository
import com.example.playlistmaker.player.data.PlaylistTrackDbConverter
import com.example.playlistmaker.player.data.dao.PlaylistTrackDao
import com.example.playlistmaker.search.domain.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val playlistDbConverter: PlaylistDbConverter,
    private val playlistTrackDbConverter: PlaylistTrackDbConverter
) : PlaylistRepository {
    override suspend fun createPlaylist(p: Playlist) {
        playlistDao.createPlaylist(playlistDbConverter.map(p))
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        playlistDao.getPlaylists().collect { entities ->
            val playlists = entities.map { entity ->
                playlistDbConverter.map(entity)
            }
            emit(playlists)
        }
    }

    override suspend fun deletePlaylist(p: Playlist) {
        playlistDao.deletePlaylist(playlistDbConverter.map(p))
    }

    override suspend fun addToPlaylistTracksTable(t: Track) {
        playlistTrackDao.insertTrackPlaylist(playlistTrackDbConverter.map(t))
    }

    override suspend fun addToPlaylist(
        t: Track,
        p: Playlist
    ) {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        val currentTrackIds = gson.fromJson<List<String>>(p.tracksId, type) ?: emptyList()
        val updatedTrackIds = currentTrackIds + t.trackId
        val updatedTracksId = gson.toJson(updatedTrackIds)
        playlistDao.addTrackToPlaylist(p.playlistId, updatedTracksId)
    }

}