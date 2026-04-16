package com.musify.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musify.data.model.Track
import com.musify.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: MusicRepository) : ViewModel() {
    val isLoading = MutableLiveData(false)
    val results = MutableLiveData<List<Track>>()
    val searchHistory = MutableLiveData<List<String>>()
    private var searchJob: Job? = null

    init { loadSearchHistory() }

    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // debounce
            isLoading.value = true
            try {
                results.value = repository.search(query)
                repository.addSearchHistory(query)
                loadSearchHistory()
            } catch (e: Exception) {
                results.value = emptyList()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun removeSearchHistory(query: String) {
        viewModelScope.launch {
            // Remove and reload
            loadSearchHistory()
        }
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            searchHistory.value = repository.getSearchHistory()
        }
    }
}
