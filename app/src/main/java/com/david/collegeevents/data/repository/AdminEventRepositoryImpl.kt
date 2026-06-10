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
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.jvm.java

class AdminEventRepositoryImpl @Inject constructor(
    private val api: ApiServices
) : AdminEventRepository {

    override fun uploadBanner(imagePart: MultipartBody.Part): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.uploadEventBanner(imagePart)
            println("📡 Upload response code: ${response.code()}")
            println("📡 Upload response body: ${response.body()}")
            println("📡 Upload error body: ${response.errorBody()?.string()}")

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.url))
            } else {
                emit(Resource.Error("Upload failed: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            val msg = when (e) {
                is java.net.SocketTimeoutException -> "Upload timed out. Check your connection or try a smaller image."
                is IOException -> "Network error during upload."
                else -> "Unexpected error: ${e.message}"
            }
            emit(Resource.Error(msg))
        }
    }

    override fun submitEvent(
        title: String, club: String, banner: String, date: String, 
        time: String, venue: String, fee: String, description: String, category: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val payload = CreateEventRequest(
                title = title, clubName = club, bannerUrl = banner,
                eventDate = date, eventTime = time, venue = venue,
                description = description, registrationFee = fee, category = category
            )
            val response = api.createNewEvent(payload)
            if (response.isSuccessful) {
                emit(Resource.Success("Event published successfully!"))
            } else {
                val errorMsg = try {
                    Gson().fromJson(response.errorBody()?.string(), GenericErrorDto::class.java).message
                } catch (e: Exception) { "Form parsing failure at remote server node." }
                emit(Resource.Error(errorMsg))
            }
        } catch (e: IOException) {
            emit(Resource.Error("Server connectivity timeout. Check infrastructure link status."))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Unexpected parsing anomaly."))
        }
    }
}