package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.FavoriteInteractor
import com.example.playlistmaker.library.domain.Playlist
import com.example.playlistmaker.library.domain.PlaylistInteractor
import com.example.playlistmaker.player.domain.PlayerState
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.sharing.data.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    companion object {
        private const val REFRESH_TIMER_DELAY = 300L
    }

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default)
    val playerState: LiveData<PlayerState> = _playerState

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _isAddingSuccessful = SingleLiveEvent<Pair<Boolean, String>>()
    val isAddingSuccessful: LiveData<Pair<Boolean, String>> = _isAddingSuccessful

    private var timerJob: Job? = null
    private val mediaPlayer = MediaPlayer()
    private val timerFormat by lazy {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }

    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }

    fun onPlayButtonClicked() {
        when (val currentState = _playerState.value) {
            is PlayerState.Playing -> pausePlayer(currentState.progressTime)
            is PlayerState.Prepared -> startPlayer()
            is PlayerState.Paused -> startPlayer()
            PlayerState.Default -> {}
        }
    }

    fun onPause() {
        when (val currentState = _playerState.value) {
            is PlayerState.Playing -> pausePlayer(currentState.progressTime)
            else -> {}
        }
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            val currentFavoriteState = _isFavorite.value ?: track.isFavorite
            if (currentFavoriteState) {
                favoriteInteractor.deleteFromFavorite(track)
                _isFavorite.postValue(false)
            } else {
                favoriteInteractor.saveToFavorite(track)
                _isFavorite.postValue(true)
            }
        }
    }

    private fun preparePlayer() {
        viewModelScope.launch {
            val favoriteTracksId = favoriteInteractor.getFavoriteTracksId()
            val isTrackFavorite = favoriteTracksId.contains(track.trackId)
            _isFavorite.postValue(isTrackFavorite)
        }

        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            _playerState.postValue(PlayerState.Prepared)
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            _playerState.postValue(PlayerState.Prepared)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        _playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimerUpdate()
    }

    private fun pausePlayer(progressTime: String) {
        mediaPlayer.pause()
        timerJob?.cancel()
        _playerState.postValue(PlayerState.Paused(progressTime))
    }

    private fun startTimerUpdate() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(REFRESH_TIMER_DELAY)
                _playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        if (mediaPlayer.currentPosition < 0) return timerFormat.format(0)
        return timerFormat.format(mediaPlayer.currentPosition) ?: timerFormat.format(0)
    }

    fun loadPlaylists(onLoaded: (List<Playlist>) -> Unit) {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .collect { playlists ->
                    onLoaded(playlists)
                }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            if (playlist.tracksId.contains(track.trackId)) {
                _isAddingSuccessful.postValue(Pair(false,playlist.playlistName))
            } else {
                playlistInteractor.addToPlaylist(track, playlist)
                playlistInteractor.addToPlaylistTracksTable(track)
                _isAddingSuccessful.postValue(Pair(true,playlist.playlistName))
            }
        }
    }
}