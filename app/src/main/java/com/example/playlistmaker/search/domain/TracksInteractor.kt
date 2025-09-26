package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.data.Track

interface TracksInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorCode: Int?)
    }
}