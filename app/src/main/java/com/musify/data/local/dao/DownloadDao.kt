package com.musify.data.local.dao
import androidx.room.*
import com.musify.data.local.entity.DownloadEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface DownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertDownload(download: DownloadEntity)
    @Delete suspend fun deleteDownload(download: DownloadEntity)
    @Query("SELECT * FROM downloads ORDER BY downloadedAt DESC") fun getAllDownloads(): Flow<List<DownloadEntity>>
    @Query("SELECT * FROM downloads ORDER BY downloadedAt DESC") suspend fun getAllDownloadsSync(): List<DownloadEntity>
    @Query("SELECT * FROM downloads WHERE trackId = :trackId LIMIT 1") suspend fun getDownloadByTrackId(trackId: String): DownloadEntity?
    @Query("SELECT EXISTS(SELECT 1 FROM downloads WHERE trackId = :trackId)") suspend fun isDownloaded(trackId: String): Boolean
    @Query("SELECT SUM(fileSize) FROM downloads") suspend fun getTotalSize(): Long?
}
