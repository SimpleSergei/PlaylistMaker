package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.data.Track
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksSearchState
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TracksSearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val stateLiveData = MutableLiveData<TracksSearchState>()
    fun observeState(): LiveData<TracksSearchState> = stateLiveData

    private var latestSearchText: String? = null
    private var historyJob: Job? = null
    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            searchRequest(changedText)
        }

    init {
        loadSearchHistory()
    }

    fun searchDebounce(changedText: String, forceRefresh: Boolean = false) {
        if (!forceRefresh && latestSearchText == changedText) return
        this.latestSearchText = changedText
        historyJob?.cancel()
        trackSearchDebounce(changedText)
    }

    fun saveToHistory(track: Track) {
        searchHistoryInteractor.saveToHistory(track)
    }

    fun clearSearchHistory() {
        searchHistoryInteractor.clearHistory()
        renderState(TracksSearchState.SearchHistoryCleared)
    }

    fun loadSearchHistory() {
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            searchHistoryInteractor.getHistory().collect { history ->
                renderState(TracksSearchState.SearchHistory(history))
            }
        }
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksSearchState.Loading)
        }
        viewModelScope.launch {
            tracksInteractor
                .searchTracks(newSearchText)
                .collect { pair -> processResult(pair.first, pair.second) }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorCode: Int?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }
        when {
            errorCode != null -> {
                renderState(TracksSearchState.Error(errorCode))
            }

            tracks.isEmpty() -> {
                renderState(TracksSearchState.Empty(emptyList()))
            }

            else -> {
                renderState(TracksSearchState.Content(tracks))
            }
        }
    }

    private fun renderState(state: TracksSearchState) {
        stateLiveData.postValue(state)
    }
}