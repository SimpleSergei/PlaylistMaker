package com.example.playlistmaker.playlists.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.playlists.data.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun createPlaylist(playlistEntity: PlaylistEntity)

    @Query("DELETE FROM playlists_table WHERE playlistId = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Query("SELECT * FROM playlists_table")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("UPDATE playlists_table SET tracksId = :trackId, tracksCount = tracksCount + 1 WHERE playlistId = :playlistId")
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: String)

    @Query("SELECT * FROM playlists_table WHERE playlistId = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity

    @Query("UPDATE playlists_table SET tracksId = :tracksId, tracksCount = tracksCount -1 WHERE playlistId = :playlistId")
    suspend fun deleteTrackById(playlistId: Long, tracksId: String,
    )
}
