package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val url: String) : ViewModel() {

    companion object {
        private const val REFRESH_TIMER_DELAY = 333L
    }

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default)
    val playerState: LiveData<PlayerState> = _playerState

    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = Runnable {
        when (_playerState.value) {
            is PlayerState.Playing -> updateProgressTime()
            else -> {}
        }
    }

    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        resetTimer()
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

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            _playerState.postValue(PlayerState.Prepared)
        }
        mediaPlayer.setOnCompletionListener {
            _playerState.postValue(PlayerState.Prepared)
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        val currentTime = when (val currentState = _playerState.value) {
            is PlayerState.Paused -> currentState.progressTime
            is PlayerState.Playing -> currentState.progressTime
            else -> "00:00"
        }
        _playerState.postValue(PlayerState.Playing(currentTime))
        startTimerUpdate()
    }

    private fun pausePlayer(progressTime: String) {
        pauseTimer()
        mediaPlayer.pause()
        _playerState.postValue(PlayerState.Paused(progressTime))
    }

    private fun startTimerUpdate() {
        updateProgressTime()
        handler.postDelayed(timerRunnable, REFRESH_TIMER_DELAY)
    }

    private fun updateProgressTime() {
        val newTime =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        _playerState.postValue(PlayerState.Playing(newTime))
        handler.postDelayed(timerRunnable, REFRESH_TIMER_DELAY)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        _playerState.postValue(PlayerState.Prepared)
    }
}