package com.musify.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musify.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    val isLoading = MutableLiveData(false)
    val error = MutableLiveData<String?>()
    val registerSuccess = MutableLiveData(false)

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            val result = authRepository.register(username, email, password)
            isLoading.value = false
            result.fold(
                onSuccess = { registerSuccess.value = true },
                onFailure = { error.value = it.message }
            )
        }
    }
}
