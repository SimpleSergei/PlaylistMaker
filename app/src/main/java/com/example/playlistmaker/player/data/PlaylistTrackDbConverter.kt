package com.example.playlistmaker.player.data

import com.example.playlistmaker.player.data.entity.PlaylistTrackEntity
import com.example.playlistmaker.search.domain.Track

class PlaylistTrackDbConverter {

    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }

    fun map(playlistTrack: PlaylistTrackEntity): Track {
        return Track(
            playlistTrack.trackId,
            playlistTrack.trackName,
            playlistTrack.artistName,
            playlistTrack.trackTimeMillis,
            playlistTrack.artworkUrl100,
            playlistTrack.collectionName,
            playlistTrack.releaseDate,
            playlistTrack.primaryGenreName,
            playlistTrack.country,
            playlistTrack.previewUrl
        )
    }
}