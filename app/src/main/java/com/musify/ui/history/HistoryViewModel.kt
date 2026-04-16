package com.musify.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.musify.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    val history = liveData(Dispatchers.IO) {
        emit(repository.getHistory())
    }
}
