package com.musify.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.musify.data.local.dao.*
import com.musify.data.local.entity.*

@Database(
    entities = [UserEntity::class, SessionEntity::class, PlaylistEntity::class,
                DownloadEntity::class, HistoryEntity::class, SearchHistoryEntity::class],
    version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun downloadDao(): DownloadDao
    abstract fun historyDao(): HistoryDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "musify_db")
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
        }
    }
}
