package com.musify.data.local.dao
import androidx.room.*
import com.musify.data.local.entity.UserEntity
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertUser(user: UserEntity): Long
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1") suspend fun getUserByEmail(email: String): UserEntity?
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1") suspend fun getUserById(id: Long): UserEntity?
    @Update suspend fun updateUser(user: UserEntity)
    @Query("DELETE FROM users WHERE id = :id") suspend fun deleteUser(id: Long)
}
