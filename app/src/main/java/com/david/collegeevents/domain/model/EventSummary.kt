package com.david.collegeevents.domain.model

data class EventSummary(
    val id: String,
    val title: String,
    val clubName: String,
    val bannerUrl: String,
    val date: String,
    val time: String,
    val venue: String,
    val registrationBadge: String
)