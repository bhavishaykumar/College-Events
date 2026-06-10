package com.david.collegeevents.domain.usecase

import com.david.collegeevents.domain.model.EventSummary
import com.david.collegeevents.domain.repository.EventRepository
import com.david.collegeevents.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val repository: EventRepository
) {
    operator fun invoke(category: String): Flow<Resource<List<EventSummary>>> {
        // All handle karne ke liye, network query string null pass hogi backend format ke according
        val queryCategory = if (category == "All") null else category
        return repository.getEvents(queryCategory)
    }
}