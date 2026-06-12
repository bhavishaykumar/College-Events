package com.david.collegeevents.presentation.createEvent

/*
data class CreateEventState(
    val isUploadingBanner: Boolean = false,
    val uploadedBannerUrl: String? = null,
    val isPublishingEvent: Boolean = false,
    val errorMessage: String? = null,
    val executionSuccess: Boolean = false
)*/

data class CreateEventState(
    val isUploadingBanner: Boolean = false,
    val uploadedBannerUrl: String? = null,
    val isPublishingEvent: Boolean = false,
    val executionSuccess: Boolean = false,
    val errorMessage: String? = null,
    val isLoadingEvent: Boolean = false,
    val prefillTitle: String? = null,
    val prefillClub: String? = null,
    val prefillDate: String? = null,
    val prefillTime: String? = null,
    val prefillVenue: String? = null,
    val prefillFee: String? = null,
    val prefillDescription: String? = null,
)
