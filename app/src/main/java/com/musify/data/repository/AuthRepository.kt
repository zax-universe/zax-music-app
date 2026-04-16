package com.musify.data.repository

import android.content.Context
import com.musify.data.local.dao.SessionDao
import com.musify.data.local.dao.UserDao
import com.musify.data.local.entity.SessionEntity
import com.musify.data.local.entity.UserEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val sessionDao: SessionDao,
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("musify_auth", Context.MODE_PRIVATE)

    suspend fun register(username: String, email: String, password: String): Result<UserEntity> {
        val existing = userDao.getUserByEmail(email)
        if (existing != null) return Result.failure(Exception("Email already in use"))
        val hash = hashPassword(password)
        val user = UserEntity(username = username, email = email, passwordHash = hash)
        val id = userDao.insertUser(user)
        val newUser = userDao.getUserById(id)!!
        createSession(newUser.id)
        return Result.success(newUser)
    }

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getUserByEmail(email)
            ?: return Result.failure(Exception("Invalid credentials"))
        if (user.passwordHash != hashPassword(password))
            return Result.failure(Exception("Invalid credentials"))
        createSession(user.id)
        return Result.success(user)
    }

    private suspend fun createSession(userId: Long) {
        val token = UUID.randomUUID().toString()
        sessionDao.insertSession(SessionEntity(token = token, userId = userId))
        prefs.edit().putString("session_token", token).apply()
    }

    suspend fun isLoggedIn(): Boolean {
        val token = prefs.getString("session_token", null) ?: return false
        val session = sessionDao.getSession(token) ?: return false
        return session.expiresAt > System.currentTimeMillis()
    }

    suspend fun getCurrentUser(): UserEntity? {
        val token = prefs.getString("session_token", null) ?: return null
        val session = sessionDao.getSession(token) ?: return null
        return userDao.getUserById(session.userId)
    }

    suspend fun getCurrentUserId(): Long? = getCurrentUser()?.id

    suspend fun logout() {
        val token = prefs.getString("session_token", null)
        if (token != null) sessionDao.deleteSession(token)
        prefs.edit().remove("session_token").apply()
    }

    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(password.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}
