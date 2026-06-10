package com.david.collegeevents.presentation.events

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.david.collegeevents.domain.usecase.GetEventsUseCase
import com.david.collegeevents.utils.Resource
import com.david.collegeevents.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    var state by mutableStateOf(EventsState())
        private set

    init {
        loadUserName()
        fetchEvents("All")
    }

    private fun loadUserName() {
        viewModelScope.launch {
            tokenManager.userNameFlow.collect { name ->
                if (!name.isNullOrBlank()) {
                    state = state.copy(currentUserName = name.substringBefore(" "))
                }
            }
        }
    }

    fun fetchEvents(category: String) {
        getEventsUseCase(category).onEach { result ->
            state = when (result) {
                is Resource.Loading -> state.copy(isLoading = true, error = null, selectedCategory = category)
                is Resource.Success -> state.copy(isLoading = false, eventsList = result.data ?: emptyList(), selectedCategory = category)
                is Resource.Error -> state.copy(isLoading = false, error = result.message, selectedCategory = category)
            }
        }.launchIn(viewModelScope)
    }
}