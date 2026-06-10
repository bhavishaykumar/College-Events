package com.david.collegeevents.domain.repository

import com.david.collegeevents.domain.model.EventDetail
import com.david.collegeevents.utils.Resource
import kotlinx.coroutines.flow.Flow

interface EventDetailsRepository {
    fun getEventDetail(eventId: String): Flow<Resource<EventDetail>>
    fun toggleRegistration(eventId: String, register: Boolean): Flow<Resource<String>>
}