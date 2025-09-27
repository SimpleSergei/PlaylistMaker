package com.example.playlistmaker.player.domain

interface PlayerState {
    data object Default : PlayerState
    data object Prepared : PlayerState
    data class Playing(val progressTime: String) : PlayerState
    data class Paused(val progressTime: String) : PlayerState
}