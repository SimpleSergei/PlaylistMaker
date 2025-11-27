package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.PlaylistInteractor
import com.example.playlistmaker.library.domain.PlaylistState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private var playlistJob: Job? = null

    private val stateLiveData = MutableLiveData<PlaylistState>()
    fun observeState(): LiveData<PlaylistState> = stateLiveData

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        playlistJob?.cancel()
        playlistJob = viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        renderState(PlaylistState.Empty)
                    } else {
                        renderState(PlaylistState.Content(playlists))
                    }
                }
        }
    }

    private fun renderState(state: PlaylistState) {
        stateLiveData.postValue(state)
    }
}