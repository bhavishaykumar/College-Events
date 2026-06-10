package com.david.collegeevents.presentation.details

import com.david.collegeevents.domain.model.EventDetail


data class EventDetailState(
    val isLoading: Boolean = false,
    val event: EventDetail? = null,
    val error: String? = null,
    val actionLoading: Boolean = false,
    val toastMessage: String? = null
)