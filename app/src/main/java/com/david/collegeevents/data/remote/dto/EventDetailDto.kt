package com.david.collegeevents.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EventDetailDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("clubName") val clubName: String,
    @SerializedName("bannerUrl") val bannerUrl: String,
    @SerializedName("date") val date: String,
    @SerializedName("time") val time: String,
    @SerializedName("venue") val venue: String,
    @SerializedName("description") val description: String,
    @SerializedName("seatAvailability") val seatAvailability: String,
    @SerializedName("registrationFee") val registrationFee: String,
    @SerializedName("isUserRegistered") val isUserRegistered: Boolean
)