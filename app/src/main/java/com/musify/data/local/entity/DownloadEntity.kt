package com.musify.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "downloads")
data class DownloadEntity(@PrimaryKey val trackId: String, val title: String, val artist: String, val albumArtUrl: String?, val localPath: String, val fileSize: Long = 0, val downloadedAt: Long = System.currentTimeMillis(), val duration: Int = 0)
