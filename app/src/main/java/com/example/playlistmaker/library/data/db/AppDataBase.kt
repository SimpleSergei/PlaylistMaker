package com.example.playlistmaker.library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.library.data.db.dao.PlaylistDao
import com.example.playlistmaker.library.data.db.dao.TrackDao
import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.player.data.dao.PlaylistTrackDao
import com.example.playlistmaker.player.data.entity.PlaylistTrackEntity

@Database(version = 3, entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class])
abstract class AppDataBase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}