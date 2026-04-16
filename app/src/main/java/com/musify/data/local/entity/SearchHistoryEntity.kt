package com.musify.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "search_history")
data class SearchHistoryEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val userId: Long, val query: String, val searchedAt: Long = System.currentTimeMillis())
