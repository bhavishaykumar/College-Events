package com.david.collegeevents.presentation.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.david.collegeevents.data.repository.EventDetailsRepositoryImpl
import com.david.collegeevents.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val repository: EventDetailsRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf(EventDetailState())
        private set

    private val eventId: String = checkNotNull(savedStateHandle["id"])

    init {
        loadEventDetails()
    }

    fun loadEventDetails() {
        repository.getEventDetail(eventId).onEach { result ->
            state = when(result) {
                is Resource.Loading -> state.copy(isLoading = true)
                is Resource.Success -> state.copy(isLoading = false, event = result.data)
                is Resource.Error -> state.copy(isLoading = false, error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    fun executeAction(register: Boolean) {
        repository.toggleRegistration(eventId, register).onEach { result ->
            when(result) {
                is Resource.Loading -> state = state.copy(actionLoading = true)
                is Resource.Success -> {
                    state = state.copy(actionLoading = false, toastMessage = result.data)
                    loadEventDetails() // Refresh matching status variables
                }
                is Resource.Error -> {
                    state = state.copy(actionLoading = false, error = result.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun clearToast() { state = state.copy(toastMessage = null) }
}