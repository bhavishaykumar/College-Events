package com.david.collegeevents.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateEventRequest(
    @SerializedName("title") val title: String,
    @SerializedName("clubName") val clubName: String,
    @SerializedName("bannerUrl") val bannerUrl: String,
    @SerializedName("eventDate") val eventDate: String,
    @SerializedName("eventTime") val eventTime: String,
    @SerializedName("venue") val venue: String,
    @SerializedName("description") val description: String,
    @SerializedName("totalSeats") val totalSeats: Int = 100, // Default constraint matching backends
    @SerializedName("registrationFee") val registrationFee: String,
    @SerializedName("category") val category: String
)

data class ImageUploadResponse(
    @SerializedName("url") val url: String
)