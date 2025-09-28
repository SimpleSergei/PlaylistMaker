package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.data.Track
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksSearchState

class TracksSearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val tracksInteractor = Creator.provideTracksInteractor()
                val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()
                TracksSearchViewModel(tracksInteractor, searchHistoryInteractor)
            }
        }
    }

    private val stateLiveData = MutableLiveData<TracksSearchState>()
    fun observeState(): LiveData<TracksSearchState> = stateLiveData

    private var latestSearchText: String? = null
    private val handler = Handler(Looper.getMainLooper())

    init {
        loadSearchHistory()
    }

    fun searchDebounce(changedText: String, forceRefresh: Boolean = false) {
        if (!forceRefresh && latestSearchText == changedText) return
        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    fun saveToHistory(track: Track) {
        searchHistoryInteractor.saveToHistory(track)
    }

    fun clearSearchHistory() {
        searchHistoryInteractor.clearHistory()
        renderState(TracksSearchState.SearchHistoryCleared)
    }

    fun loadSearchHistory() {
        searchHistoryInteractor.getHistory(object : SearchHistoryInteractor.HistoryConsumer {
            override fun consume(searchHistory: List<Track>?) {
                val history = searchHistory?.reversed() ?: emptyList()
                renderState(TracksSearchState.SearchHistory(history))
            }
        })
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksSearchState.Loading)
        }
        tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorCode: Int?) {
                handler.post {
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
                            renderState(TracksSearchState.Content(track = tracks))
                        }
                    }
                }
            }
        })
    }
    private fun renderState(state: TracksSearchState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
}