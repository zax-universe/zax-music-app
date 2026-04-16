package com.musify.data.local.dao
import androidx.room.*
import com.musify.data.local.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertPlaylist(playlist: PlaylistEntity): Long
    @Update suspend fun updatePlaylist(playlist: PlaylistEntity)
    @Delete suspend fun deletePlaylist(playlist: PlaylistEntity)
    @Query("SELECT * FROM playlists WHERE userId = :userId ORDER BY updatedAt DESC") fun getPlaylistsByUser(userId: Long): Flow<List<PlaylistEntity>>
    @Query("SELECT * FROM playlists WHERE id = :id LIMIT 1") suspend fun getPlaylistById(id: Long): PlaylistEntity?
}
