package com.example.playlistmaker.playlists.ui

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlists.domain.PlaylistDetailsState
import com.example.playlistmaker.playlists.domain.PlaylistInteractor
import com.example.playlistmaker.playlists.domain.ShareEvent
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.sharing.data.SingleLiveEvent
import com.example.playlistmaker.utils.TextFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsViewModel(
    private val playlistId: Long,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlistDetailsState = MutableLiveData<PlaylistDetailsState>()
    val playlistDetailsState: LiveData<PlaylistDetailsState> = _playlistDetailsState

    private val _shareEvent = SingleLiveEvent<ShareEvent>()
    val shareEvent: LiveData<ShareEvent> = _shareEvent

    init {
        getPlaylistDetails()
    }


    fun sharePlaylist() {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            val tracks = playlistInteractor.getTracksByIds(playlistId)

            if (tracks.isEmpty()) {
                _shareEvent.postValue(ShareEvent.EmptyPlaylist)
            } else {
                val shareText =
                    createShareText(playlist.playlistName, playlist.playlistDescription, tracks)

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    putExtra(Intent.EXTRA_SUBJECT, playlist.playlistName)
                }

                _shareEvent.postValue(ShareEvent.SharePlaylist(shareIntent))
            }
        }
    }


    fun getPlaylistDetails() {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            val tracks = playlistInteractor.getTracksByIds(playlistId)
            val totalDuration = tracks.sumOf { it.trackTimeMillis }
            if (tracks.isEmpty()) {
                _playlistDetailsState.value = PlaylistDetailsState.Empty(
                    playlist = playlist,
                    totalDuration = totalDuration
                )

            } else {
                _playlistDetailsState.value = PlaylistDetailsState.Content(
                    playlist = playlist,
                    tracks = tracks,
                    totalDuration = totalDuration
                )
            }
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlistId)
            _playlistDetailsState.value = PlaylistDetailsState.Deleted
        }
    }

    fun deleteTrackFromPlaylist(track: Track) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackById(playlistId, track.trackId)
            getPlaylistDetails()
            playlistInteractor.deleteTrackFromDBById(track.trackId)
        }
    }

    private fun createShareText(
        playlistName: String,
        playlistDescription: String,
        tracks: List<Track>
    ): String {
        val durationFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        var text = playlistName + "\n"

        if (playlistDescription.isNotEmpty()) {
            text += playlistDescription + "\n"
        }

        text += TextFormatter.tracksCountFormat(tracks.size) + "\n\n"

        for (i in 0 until tracks.size) {
            val track = tracks[i]
            val duration = durationFormat.format(track.trackTimeMillis)

            text += "${i + 1}. ${track.artistName} - ${track.trackName} ($duration)"

            if (i != tracks.size - 1) {
                text += "\n"
            }
        }

        return text
    }
}