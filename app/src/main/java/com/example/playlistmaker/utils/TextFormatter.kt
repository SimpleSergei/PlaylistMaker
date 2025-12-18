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

        fun formatDurationMinutes(millis: Long): String {
            val totalMinutes = millis / 60000
            return when {
                totalMinutes == 0L -> "0 минут"
                totalMinutes % 10 == 1L && totalMinutes % 100 != 11L -> "$totalMinutes минута"
                totalMinutes % 10 in 2..4 && totalMinutes % 100 !in 12..14 -> "$totalMinutes минуты"
                else -> "$totalMinutes минут"
            }
        }
    }
}