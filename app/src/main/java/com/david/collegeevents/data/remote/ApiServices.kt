package com.david.collegeevents.data.remote

import com.david.collegeevents.data.remote.dto.AuthResponseDto
import com.david.collegeevents.data.remote.dto.CreateEventRequest
import com.david.collegeevents.data.remote.dto.EventDTOs
import com.david.collegeevents.data.remote.dto.EventDetailDto
import com.david.collegeevents.data.remote.dto.GenericErrorDto
import com.david.collegeevents.data.remote.dto.ImageUploadResponse
import com.david.collegeevents.data.remote.dto.LoginRequest
import com.david.collegeevents.data.remote.dto.RegisterRequest
import com.david.collegeevents.data.remote.dto.StudentProfileDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServices {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponseDto>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponseDto>

    @GET("api/events")
    suspend fun getEvents(
        @Query("category") category: String?
    ): Response<List<EventDTOs>>

    @GET("api/user/profile")
    suspend fun getProfile(): Response<StudentProfileDto>

    @GET("api/events/{id}")
    suspend fun getEventDetail(@Path("id") eventId: String): Response<EventDetailDto>

    @POST("api/events/{id}/register")
    suspend fun registerForEvent(@Path("id") eventId: String): Response<GenericErrorDto>

    @POST("api/events/{id}/deregister")
    suspend fun deregisterFromEvent(@Path("id") eventId: String): Response<GenericErrorDto>

    @Multipart
    @POST("images/upload")
    suspend fun uploadEventBanner(
        @Part image: MultipartBody.Part
    ): Response<ImageUploadResponse>

    @DELETE("images/{imageName}")
    suspend fun deleteServerImage(
        @Path("imageName") imageName: String
    ): Response<Map<String, String>>

    @POST("api/events")
    suspend fun createNewEvent(
        @Body request: CreateEventRequest
    ): Response<GenericErrorDto>

    @PUT("api/events/{id}")
    suspend fun updateExistingEvent(
        @Path("id") eventId: String,
        @Body request: CreateEventRequest
    ): Response<GenericErrorDto>

    @DELETE("api/events/{id}")
    suspend fun deleteExistingEvent(
        @Path("id") eventId: String
    ): Response<GenericErrorDto>

}