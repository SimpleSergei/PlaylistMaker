package com.example.playlistmaker.playlists.domain

data class Playlist (
    val playlistId: Long,
    val playlistName: String,
    val playlistDescription: String,
    val playlistCoverPath: String,
    val tracksId: String,
    val tracksCount: Int
)