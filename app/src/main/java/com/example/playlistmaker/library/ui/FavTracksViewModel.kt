package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.FavoriteInteractor
import com.example.playlistmaker.library.domain.FavoriteTracksState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavTracksViewModel(private val favoriteInteractor: FavoriteInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteTracksState>()
    fun observeState(): LiveData<FavoriteTracksState> = stateLiveData

    init {
        getFavoriteTracks()
    }

    private fun getFavoriteTracks() {
        viewModelScope.launch {
            favoriteInteractor.getFavoriteTracks().collectLatest { tracks ->
                if (tracks.isEmpty()) {
                    renderState(FavoriteTracksState.Empty)
                } else {
                    renderState(FavoriteTracksState.Content(tracks))
                }
            }
        }
    }
    private fun renderState(state: FavoriteTracksState) {
        stateLiveData.postValue(state)
    }
}