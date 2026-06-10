package com.david.collegeevents.presentation.events

import com.david.collegeevents.domain.model.EventSummary


data class EventsState(
    val isLoading: Boolean = false,
    val eventsList: List<EventSummary> = emptyList(),
    val error: String? = null,
    val selectedCategory: String = "All",
    val currentUserName: String = "Student"
)