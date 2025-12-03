package com.example.playlistmaker.playlists.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlists.domain.Playlist
import com.example.playlistmaker.playlists.domain.PlaylistData
import com.example.playlistmaker.playlists.domain.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistEditViewModel(
    private val playlistId: Long,
    private val playlistInteractor: PlaylistInteractor
) :
    PlaylistCreateViewModel(playlistInteractor) {

    private val _playlistData = MutableLiveData<PlaylistData>()
    val playlistData: LiveData<PlaylistData> = _playlistData

    private var originalPlaylist: Playlist? = null


    fun loadPlaylist() {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            originalPlaylist = playlist

            _playlistData.value = PlaylistData(
                name = playlist.playlistName,
                description = playlist.playlistDescription,
                imageUri = if (playlist.playlistCoverPath.isNotEmpty()) {
                    Uri.parse(playlist.playlistCoverPath)
                } else {
                    null
                }
            )
        }
    }

    override fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        context: Context,
        uri: Uri?
    ) {
        viewModelScope.launch {
            val coverPath = if (uri != null) {
                val internalUri = playlistInteractor.copyImageToInternalStorage(context, uri)
                internalUri.toString()
            } else {
                originalPlaylist?.playlistCoverPath ?: ""
            }

            originalPlaylist?.let { playlist ->
                val updatedPlaylist = playlist.copy(
                    playlistName = playlistName,
                    playlistDescription = playlistDescription,
                    playlistCoverPath = coverPath
                )

                playlistInteractor.updatePlaylist(updatedPlaylist)
            }
        }
    }
}