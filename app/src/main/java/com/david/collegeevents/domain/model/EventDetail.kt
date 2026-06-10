package com.david.collegeevents.domain.model

data class EventDetail(
    val id: String,
    val title: String,
    val clubName: String,
    val bannerUrl: String,
    val date: String,
    val time: String,
    val venue: String,
    val description: String,
    val seatAvailability: String,
    val registrationFee: String,
    val isUserRegistered: Boolean
)