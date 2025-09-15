package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.SEARCH_HISTORY_KEY
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryInteractorImpl(private val sharedPreferences: SharedPreferences) :
    SearchHistoryInteractor {
    private var json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)
    private val type = object : TypeToken<ArrayList<Track>>() {}.type
    private var tracksSearchHistory = ArrayList<Track>()

    init {
        if (json != null) tracksSearchHistory.addAll(Gson().fromJson(json, type))
    }

    override fun addTrackToHistory(track: Track) {
        if (tracksSearchHistory.size == 10) tracksSearchHistory.removeAt(0)
        for (i in 0..tracksSearchHistory.size - 1) if (tracksSearchHistory[i].trackId == track.trackId) {
            tracksSearchHistory.removeAt(i)
            break
        }
        tracksSearchHistory.add(track)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, Gson().toJson(tracksSearchHistory))
            .apply()
    }

    override fun clearTrackHistory() {
        tracksSearchHistory.clear()
        sharedPreferences.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }

    override fun getSearchHistory(): ArrayList<Track>? {
        json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)
        return if (json == null) {
            null
        } else {
            Gson().fromJson(json, type)
        }
    }
}