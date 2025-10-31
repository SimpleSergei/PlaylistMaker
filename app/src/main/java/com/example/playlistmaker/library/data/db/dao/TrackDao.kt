package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavTrack(trackEntity: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteFavTrack(trackEntity: TrackEntity)

    @Query("SELECT * FROM tracks_table")
    suspend fun getFavoriteTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM tracks_table")
    suspend fun getFavoriteTracksId(): List<String>
}