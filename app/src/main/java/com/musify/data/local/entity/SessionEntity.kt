package com.musify.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "sessions")
data class SessionEntity(@PrimaryKey val token: String, val userId: Long, val createdAt: Long = System.currentTimeMillis(), val expiresAt: Long = System.currentTimeMillis() + 30L*24*60*60*1000)
