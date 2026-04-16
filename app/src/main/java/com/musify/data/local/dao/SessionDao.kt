package com.musify.data.local.dao
import androidx.room.*
import com.musify.data.local.entity.SessionEntity
@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertSession(session: SessionEntity)
    @Query("SELECT * FROM sessions WHERE token = :token LIMIT 1") suspend fun getSession(token: String): SessionEntity?
    @Query("SELECT * FROM sessions ORDER BY createdAt DESC LIMIT 1") suspend fun getLatestSession(): SessionEntity?
    @Query("DELETE FROM sessions WHERE token = :token") suspend fun deleteSession(token: String)
    @Query("DELETE FROM sessions") suspend fun deleteAllSessions()
}
