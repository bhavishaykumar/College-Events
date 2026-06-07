package com.david.collegeevents.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.david.collegeevents.domain.usecase.LoginUseCase
import com.david.collegeevents.domain.usecase.RegisterUseCase
import com.david.collegeevents.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    var state by mutableStateOf(AuthState())
        private set

    fun login(enrollment: String, pass: String) {
        loginUseCase(enrollment, pass).onEach { result ->
            state = when (result) {
                is Resource.Loading -> AuthState(isLoading = true)
                is Resource.Success -> AuthState(isSuccess = true, studentName = result.data?.userName ?: "")
                is Resource.Error -> AuthState(error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    fun register(name: String, enr: String, branch: String, email: String, pass: String) {
        registerUseCase(name, enr, branch, email, pass).onEach { result ->
            state = when (result) {
                is Resource.Loading -> AuthState(isLoading = true)
                is Resource.Success -> AuthState(isSuccess = true, studentName = result.data?.userName ?: "")
                is Resource.Error -> AuthState(error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    fun clearError() {
        state = state.copy(error = null)
    }
}