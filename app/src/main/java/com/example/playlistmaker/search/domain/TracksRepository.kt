package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.data.Resource
import com.example.playlistmaker.search.data.Track

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}