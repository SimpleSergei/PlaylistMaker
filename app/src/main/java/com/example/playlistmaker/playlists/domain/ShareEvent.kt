package com.example.playlistmaker.playlists.domain

import android.content.Intent

sealed interface ShareEvent {
    object EmptyPlaylist : ShareEvent
    data class SharePlaylist(val intent: Intent) : ShareEvent
}