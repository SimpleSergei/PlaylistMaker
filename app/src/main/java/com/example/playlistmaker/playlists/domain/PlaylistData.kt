package com.example.playlistmaker.playlists.domain

import android.net.Uri

data class PlaylistData(
    val name: String,
    val description: String,
    val imageUri: Uri?
)