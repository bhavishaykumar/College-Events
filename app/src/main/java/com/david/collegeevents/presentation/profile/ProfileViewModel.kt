package com.david.collegeevents.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.david.collegeevents.domain.usecase.GetUserProfileUseCase
import com.david.collegeevents.utils.Resource
import com.david.collegeevents.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    var state by mutableStateOf(ProfileState())
        private set

    init {
        getProfile()
    }

    fun getProfile() {
        getUserProfileUseCase().onEach { result ->
            state = when (result) {
                is Resource.Loading -> state.copy(isLoading = true, error = null)
                is Resource.Success -> state.copy(isLoading = false, profileData = result.data)
                is Resource.Error -> state.copy(isLoading = false, error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearSession() // Local DataStore clear edge case triggered!
            state = state.copy(isLoggedOut = true)
        }
    }
}