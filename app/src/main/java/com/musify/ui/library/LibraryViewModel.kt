package com.musify.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.musify.data.local.dao.PlaylistDao
import com.musify.data.local.entity.PlaylistEntity
import com.musify.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val authRepository: AuthRepository
) : ViewModel() {

    val playlists = kotlinx.coroutines.flow.flow {
        val userId = authRepository.getCurrentUserId() ?: return@flow
        playlistDao.getPlaylistsByUser(userId).collect { emit(it) }
    }.asLiveData()

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            playlistDao.insertPlaylist(PlaylistEntity(userId = userId, name = name))
        }
    }

    fun deletePlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch { playlistDao.deletePlaylist(playlist) }
    }

    fun filterPlaylists() { /* already showing playlists */ }
}
