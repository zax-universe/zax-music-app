package com.musify.ui.downloads

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.musify.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    val downloads = liveData(Dispatchers.IO) {
        emit(repository.getDownloadedTracks())
    }

    val storageUsed = MutableLiveData<String>()

    init {
        calculateStorage()
    }

    private fun calculateStorage() {
        viewModelScope.launch(Dispatchers.IO) {
            // Placeholder — real impl would check download directory
            storageUsed.postValue("0 MB")
        }
    }
}
