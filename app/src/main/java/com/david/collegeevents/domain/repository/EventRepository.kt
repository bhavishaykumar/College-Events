package com.david.collegeevents.domain.repository

import com.david.collegeevents.domain.model.EventSummary
import com.david.collegeevents.utils.Resource
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getEvents(category: String?): Flow<Resource<List<EventSummary>>>
}