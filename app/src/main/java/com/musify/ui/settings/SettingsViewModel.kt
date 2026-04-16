package com.musify.ui.settings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musify.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val streamingQuality = MutableLiveData<String>()
    val downloadQuality = MutableLiveData<String>()
    val cacheSize = MutableLiveData<String>()
    val logoutEvent = MutableLiveData<Boolean>()

    private val prefs = context.getSharedPreferences("musify_prefs", Context.MODE_PRIVATE)

    init {
        loadUserInfo()
        loadPreferences()
        calculateCacheSize()
    }

    private fun loadUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = authRepository.getCurrentUser()
            username.postValue(user?.username ?: "User")
            email.postValue(user?.email ?: "")
        }
    }

    private fun loadPreferences() {
        streamingQuality.value = prefs.getString("streaming_quality", "Normal (96 kbps)") ?: "Normal (96 kbps)"
        downloadQuality.value = prefs.getString("download_quality", "High (160 kbps)") ?: "High (160 kbps)"
    }

    fun setStreamingQuality(quality: String) {
        prefs.edit().putString("streaming_quality", quality).apply()
        streamingQuality.value = quality
    }

    fun setDownloadQuality(quality: String) {
        prefs.edit().putString("download_quality", quality).apply()
        downloadQuality.value = quality
    }

    fun clearCache() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                context.cacheDir.deleteRecursively()
                calculateCacheSize()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun calculateCacheSize() {
        viewModelScope.launch(Dispatchers.IO) {
            val size = getFolderSize(context.cacheDir)
            val mb = size / (1024 * 1024)
            cacheSize.postValue("$mb MB")
        }
    }

    private fun getFolderSize(dir: java.io.File): Long {
        var size = 0L
        if (dir.isDirectory) {
            dir.listFiles()?.forEach { size += getFolderSize(it) }
        } else {
            size = dir.length()
        }
        return size
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.logout()
            logoutEvent.postValue(true)
        }
    }
}
