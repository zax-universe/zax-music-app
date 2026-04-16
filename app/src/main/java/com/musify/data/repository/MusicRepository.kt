package com.musify.data.repository

import android.content.Context
import com.musify.data.local.dao.DownloadDao
import com.musify.data.local.dao.HistoryDao
import com.musify.data.local.dao.SearchHistoryDao
import com.musify.data.local.entity.HistoryEntity
import com.musify.data.local.entity.SearchHistoryEntity
import com.musify.data.model.Track
import com.musify.data.remote.api.DeezerApi
import com.musify.data.remote.api.JamendoApi
import com.musify.data.remote.api.LastFmApi
import com.musify.data.remote.dto.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val deezerApi: DeezerApi,
    private val jamendoApi: JamendoApi,
    private val lastFmApi: LastFmApi,
    private val downloadDao: DownloadDao,
    private val historyDao: HistoryDao,
    private val searchHistoryDao: SearchHistoryDao,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) {

    suspend fun search(query: String): List<Track> {
        val results = mutableListOf<Track>()
        // Try Deezer first
        try {
            val resp = deezerApi.search(query)
            resp.data?.map { it.toTrack() }?.let { results.addAll(it) }
            if (results.isNotEmpty()) return results
        } catch (e: Exception) { /* fallback */ }
        // Try Jamendo
        try {
            val resp = jamendoApi.search(query = query)
            resp.results?.map { it.toTrack() }?.let { results.addAll(it) }
            if (results.isNotEmpty()) return results
        } catch (e: Exception) { /* fallback */ }
        // Try LastFM
        try {
            val resp = lastFmApi.search(query)
            resp.results?.trackMatches?.track?.map { it.toTrack() }?.let { results.addAll(it) }
        } catch (e: Exception) { /* fallback */ }
        return results
    }

    suspend fun getTrending(): List<Track> {
        return try {
            val resp = deezerApi.getTopTracks()
            resp.data?.map { it.toTrack() } ?: emptyList()
        } catch (e: Exception) {
            try {
                val resp = jamendoApi.getTrending()
                resp.results?.map { it.toTrack() } ?: emptyList()
            } catch (e2: Exception) { emptyList() }
        }
    }

    suspend fun getNewReleases(): List<Track> = getTrending().shuffled().take(10)
    suspend fun getFeatured(): List<Track> = getTrending().take(5)

    suspend fun getHistory(): List<Track> {
        val userId = authRepository.getCurrentUserId() ?: return emptyList()
        return historyDao.getHistory(userId).map {
            Track(it.trackId, it.title, it.artist, albumArtUrl = it.albumArtUrl, previewUrl = it.previewUrl)
        }
    }

    suspend fun addToHistory(track: Track) {
        val userId = authRepository.getCurrentUserId() ?: return
        historyDao.insertHistory(HistoryEntity(
            userId = userId, trackId = track.id, title = track.title,
            artist = track.artist, albumArtUrl = track.albumArtUrl, previewUrl = track.previewUrl
        ))
    }

    suspend fun getSearchHistory(): List<String> {
        val userId = authRepository.getCurrentUserId() ?: return emptyList()
        return searchHistoryDao.getSearchHistory(userId).map { it.query }
    }

    suspend fun addSearchHistory(query: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        searchHistoryDao.insertSearch(SearchHistoryEntity(userId = userId, query = query))
    }

    suspend fun getDownloadedTracks(): List<Track> {
        return downloadDao.getAllDownloadsSync().map {
            Track(it.trackId, it.title, it.artist, albumArtUrl = it.albumArtUrl,
                previewUrl = it.localPath, duration = it.duration, isDownloaded = true)
        }
    }

    suspend fun isDownloaded(trackId: String) = downloadDao.isDownloaded(trackId)
}
