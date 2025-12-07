package com.example.playlistmaker.playlists.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.playlists.data.converters.PlaylistDbConverter
import com.example.playlistmaker.playlists.data.converters.PlaylistTrackDbConverter
import com.example.playlistmaker.playlists.data.dao.PlaylistDao
import com.example.playlistmaker.playlists.data.dao.PlaylistTrackDao
import com.example.playlistmaker.playlists.domain.Playlist
import com.example.playlistmaker.playlists.domain.PlaylistRepository
import com.example.playlistmaker.search.domain.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val playlistDbConverter: PlaylistDbConverter,
    private val playlistTrackDbConverter: PlaylistTrackDbConverter
) : PlaylistRepository {
    override suspend fun createPlaylist(name: String, description: String, coverPath: String) {
        val playlist = Playlist(
            playlistId = 0,
            playlistName = name,
            playlistDescription = description,
            playlistCoverPath = coverPath,
            tracksId = "",
            tracksCount = 0
        )
        playlistDao.createPlaylist(playlistDbConverter.map(playlist))
    }


    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        playlistDao.getAllPlaylists().collect { entities ->
            val playlists = entities.map { entity ->
                playlistDbConverter.map(entity)
            }
            emit(playlists)
        }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        val playlistEntity = playlistDao.getPlaylistById(playlistId)
        playlistDao.deletePlaylist(playlistId)
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        val trackIds = gson.fromJson<List<String>>(playlistEntity.tracksId, type) ?: emptyList()
        trackIds.forEach { trackId ->
            deleteTrackFromDBById(trackId)
        }
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
        val updatedTrackIds = listOf(t.trackId) + currentTrackIds
        val updatedTracksId = gson.toJson(updatedTrackIds)
        playlistDao.addTrackToPlaylist(p.playlistId, updatedTracksId)
    }

    override fun copyImageToInternalStorage(context: Context, uri: Uri): Uri {
        val contentResolver = context.contentResolver
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "PlaylistCoverAlbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val timeStamp = System.currentTimeMillis()
        val file = File(filePath, "playlist_cover_$timeStamp")
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return Uri.fromFile(file)
    }

    override suspend fun getPlaylistById(id: Long): Playlist {
        return playlistDbConverter.map(playlistDao.getPlaylistById(id))
    }

    override suspend fun getTracksByIds(playlistId: Long): List<Track> {
        val playlistEntity = playlistDao.getPlaylistById(playlistId) ?: return emptyList()
        val trackIds = if (playlistEntity.tracksId.isNotEmpty()) {
            Gson().fromJson(playlistEntity.tracksId, Array<String>::class.java).toList()
        } else {
            emptyList()
        }

        return if (trackIds.isNotEmpty()) {
            val trackEntities = playlistTrackDao.getTracksByIds(trackIds)
            val trackMap = trackEntities.associateBy { it.trackId }
            trackIds.mapNotNull { trackId ->
                trackMap[trackId]?.let { playlistTrackDbConverter.map(it) }
            }
        } else {
            emptyList()
        }
    }

    override suspend fun deleteTrackById(playlistId: Long, trackId: String) {
        val playlist = playlistDao.getPlaylistById(playlistId)

        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        val currentTrackIds = gson.fromJson<List<String>>(playlist.tracksId, type) ?: emptyList()

        val updatedTrackIds = currentTrackIds.filter { it != trackId }

        if (updatedTrackIds.size == currentTrackIds.size) {
            return
        }

        val updatedTracksId = gson.toJson(updatedTrackIds)

        playlistDao.deleteTrackById(playlistId, updatedTracksId)
    }

    override suspend fun deleteTrackFromDBById(trackId: String) {
        val allPlaylists = playlistDao.getAllPlaylists().first()

        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        val allTrackIdsInPlaylists = mutableSetOf<String>()

        allPlaylists.forEach { playlistEntity ->
            if (playlistEntity.tracksId.isNotEmpty()) {
                val trackIds =
                    gson.fromJson<List<String>>(playlistEntity.tracksId, type) ?: emptyList()
                allTrackIdsInPlaylists.addAll(trackIds)
            }
        }

        if (!allTrackIdsInPlaylists.contains(trackId)) {
            playlistTrackDao.deleteTrackFromDBById(trackId)
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        playlistDao.createPlaylist(playlistEntity)
    }
}
