package com.musify.data.local.dao
import androidx.room.*
import com.musify.data.local.entity.SearchHistoryEntity
@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertSearch(search: SearchHistoryEntity)
    @Query("SELECT * FROM search_history WHERE userId = :userId ORDER BY searchedAt DESC LIMIT 20") suspend fun getSearchHistory(userId: Long): List<SearchHistoryEntity>
    @Query("DELETE FROM search_history WHERE id = :id") suspend fun deleteSearch(id: Long)
    @Query("DELETE FROM search_history WHERE userId = :userId") suspend fun clearAll(userId: Long)
}
