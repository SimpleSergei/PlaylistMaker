package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlists.domain.PlaylistInteractor
import com.example.playlistmaker.library.domain.PlaylistLibraryState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistsLibraryViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private var playlistJob: Job? = null

    private val stateLiveData = MutableLiveData<PlaylistLibraryState>()
    fun observeState(): LiveData<PlaylistLibraryState> = stateLiveData

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        playlistJob?.cancel()
        playlistJob = viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        renderState(PlaylistLibraryState.Empty)
                    } else {
                        renderState(PlaylistLibraryState.Content(playlists))
                    }
                }
        }
    }

    private fun renderState(state: PlaylistLibraryState) {
        stateLiveData.postValue(state)
    }
}