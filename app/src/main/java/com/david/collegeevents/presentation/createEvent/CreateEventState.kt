package com.david.collegeevents.presentation.createEvent

data class CreateEventState(
    val isUploadingBanner: Boolean = false,
    val uploadedBannerUrl: String? = null,
    val isPublishingEvent: Boolean = false,
    val errorMessage: String? = null,
    val executionSuccess: Boolean = false
)