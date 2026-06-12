package com.david.collegeevents.data.repository

import com.david.collegeevents.data.remote.ApiServices
import com.david.collegeevents.data.remote.dto.CreateEventRequest
import com.david.collegeevents.data.remote.dto.GenericErrorDto
import com.david.collegeevents.domain.repository.AdminEventRepository
import com.david.collegeevents.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class AdminEventRepositoryImpl @Inject constructor(
    private val api: ApiServices
) : AdminEventRepository {

    override fun uploadBanner(imagePart: MultipartBody.Part): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.uploadEventBanner(imagePart)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.url))
            } else {
                emit(Resource.Error("Image validation or upload clearance rejected by host server."))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Storage subsystem processing connection failed."))
        }
    }

    override fun deleteImage(imageUrl: String): Flow<Resource<Boolean>> = flow {
        try {
            // Extract file name from absolute path URL (e.g. http://.../uploads/banner_xyz.jpg -> banner_xyz.jpg)
            val imageName = imageUrl.substringAfterLast("/")
            if (imageName.isNotBlank() && !imageUrl.contains("unsplash.com")) {
                api.deleteServerImage(imageName)
                emit(Resource.Success(true))
            } else {
                emit(Resource.Success(false)) // Skip if it's a mock stock photo URL
            }
        } catch (e: Exception) {
            emit(Resource.Error("Failed to purge obsolete file asset from storage."))
        }
    }

    override fun submitEvent(
        title: String, club: String, banner: String, date: String,
        time: String, venue: String, fee: String, description: String, category: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val payload = CreateEventRequest(
                title,
                club,
                banner,
                date,
                time,
                venue,
                description,
                100,
                fee,
                category
            )
            val response = api.createNewEvent(payload)
            if (response.isSuccessful) {
                emit(Resource.Success("Event published successfully!"))
            } else {
                val errorMsg = parseErrorBody(response.errorBody()?.string())
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Infrastructure transmission connection failed."))
        }
    }

    override fun modifyEvent(
        eventId: String, title: String, club: String, banner: String, date: String,
        time: String, venue: String, fee: String, description: String, category: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val payload = CreateEventRequest(title, club, banner, date, time, venue, description, 100, fee, category)
            val response = api.updateExistingEvent(eventId, payload)
            if (response.isSuccessful) {
                emit(Resource.Success("Event updated safely!"))
            } else {
                val errorMsg = parseErrorBody(response.errorBody()?.string())
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Modification pipeline transmission failed."))
        }
    }

    override fun dropEvent(eventId: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.deleteExistingEvent(eventId)
            if (response.isSuccessful) {
                emit(Resource.Success("Event deleted completely!"))
            } else {
                val errorMsg = parseErrorBody(response.errorBody()?.string())
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Drop execution command handling rejected."))
        }
    }

    private fun parseErrorBody(json: String?): String {
        return try {
            Gson().fromJson(json, GenericErrorDto::class.java).message
        } catch (e: Exception) { "Form processing failure at remote node." }
    }
}