package com.musify.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "history")
data class HistoryEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val userId: Long, val trackId: String, val title: String, val artist: String, val albumArtUrl: String?, val previewUrl: String?, val playedAt: Long = System.currentTimeMillis())
