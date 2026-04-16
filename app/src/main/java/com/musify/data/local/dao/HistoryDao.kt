package com.musify.data.local.dao
import androidx.room.*
import com.musify.data.local.entity.HistoryEntity
@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertHistory(history: HistoryEntity)
    @Query("SELECT * FROM history WHERE userId = :userId ORDER BY playedAt DESC LIMIT 50") suspend fun getHistory(userId: Long): List<HistoryEntity>
    @Query("DELETE FROM history WHERE userId = :userId") suspend fun clearHistory(userId: Long)
}
