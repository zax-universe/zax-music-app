package com.musify.ui.library

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musify.data.local.dao.PlaylistDao
import com.musify.data.local.entity.PlaylistEntity
import com.musify.data.model.Track
import com.musify.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val musicRepository: MusicRepository
) : ViewModel() {
    val playlist = MutableLiveData<PlaylistEntity?>()
    val tracks = MutableLiveData<List<Track>>(emptyList())

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val p = playlistDao.getPlaylistById(playlistId)
            playlist.value = p
            if (p != null && p.trackIds.isNotEmpty()) {
                // Playlist tracks would be loaded from saved IDs
                // For demo, search for something based on playlist name
                try { tracks.value = musicRepository.search(p.name).take(10) } catch (e: Exception) { }
            }
        }
    }
}
