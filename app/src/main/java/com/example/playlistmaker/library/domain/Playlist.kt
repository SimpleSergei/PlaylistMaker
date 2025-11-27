package com.example.playlistmaker.library.domain

data class Playlist (
    val playlistId: Long,
    val playlistName: String,
    val playlistDescription: String,
    val playlistCoverPath: String,
    val tracksId: String,
    val tracksCount: Int
)