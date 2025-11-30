package com.example.playlistmaker.utils

class TextFormatter {
    companion object{
        fun tracksCountFormat(count: Int): String {
            val dig10 = count % 10
            val dig100 = count % 100
            return when {
                dig10 == 1 && dig100 != 11 -> "$count трек"
                dig10 in 2..4 && dig100 !in 12..14 -> "$count трека"
                else -> "$count треков"
            }
        }
    }
}