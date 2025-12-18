package com.example.playlistmaker.playlists.data.converters

import com.example.playlistmaker.playlists.data.entity.PlaylistEntity
import com.example.playlistmaker.playlists.domain.Playlist

class PlaylistDbConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.playlistCoverPath,
            playlist.tracksId,
            playlist.tracksCount
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.playlistCoverPath,
            playlist.tracksId,
            playlist.tracksCount
        )
    }
}