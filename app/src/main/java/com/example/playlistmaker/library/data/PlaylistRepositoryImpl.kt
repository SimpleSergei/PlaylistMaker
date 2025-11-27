package com.example.playlistmaker.library.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
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

}