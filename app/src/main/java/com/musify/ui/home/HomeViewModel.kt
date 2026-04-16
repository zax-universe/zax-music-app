package com.musify.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musify.data.model.Track
import com.musify.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: MusicRepository) : ViewModel() {
    val isLoading = MutableLiveData(false)
    val error = MutableLiveData<String?>()
    val recentTracks = MutableLiveData<List<Track>>(emptyList())
    val trendingTracks = MutableLiveData<List<Track>>(emptyList())
    val featuredTracks = MutableLiveData<List<Track>>(emptyList())
    val newReleases = MutableLiveData<List<Track>>(emptyList())

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                val trending = repository.getTrending()
                trendingTracks.value = trending
                featuredTracks.value = repository.getFeatured()
                newReleases.value = repository.getNewReleases()
                recentTracks.value = repository.getHistory().take(6)
                    .ifEmpty { trending.shuffled().take(6) }
            } catch (e: Exception) {
                error.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }
}
