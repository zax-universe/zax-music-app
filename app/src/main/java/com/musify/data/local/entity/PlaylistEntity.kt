package com.musify.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "playlists")
data class PlaylistEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val userId: Long, val name: String, val description: String? = null, val coverArtUrl: String? = null, val trackIds: String = "", val createdAt: Long = System.currentTimeMillis(), val updatedAt: Long = System.currentTimeMillis())
