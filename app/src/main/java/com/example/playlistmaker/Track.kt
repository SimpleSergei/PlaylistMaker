package com.example.playlistmaker

data class Track(
    val trackId: String, //ID композиции
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека
    val artworkUrl100: String // Ссылка на изображение обложки
)
