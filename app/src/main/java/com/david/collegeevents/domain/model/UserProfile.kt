package com.david.collegeevents.domain.model

data class UserProfile(
    val fullName: String,
    val enrollmentNumber: String,
    val branchDepartment: String,
    val totalEvents: Int,
    val certificatesCount: Int,
    val registeredEvents: List<EventSummary>
)