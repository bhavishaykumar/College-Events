package com.david.collegeevents.presentation.createEvent

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.david.collegeevents.domain.repository.AdminEventRepository
import com.david.collegeevents.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val repository: AdminEventRepository,
    private val application: Application // 👈 Application context inject kiya temporary files create karne ke liye
) : ViewModel() {

    var state by mutableStateOf(CreateEventState())
        private set

    private val _event = MutableSharedFlow<CreateEventUiEvent>()
    val event = _event.asSharedFlow()

    // 🔴 PRODUCTION IMAGE UPLOAD PIPELINE: Converts Uri to physical binary body file securely
    fun uploadBannerImage(uri: Uri) {
        viewModelScope.launch {
            try {
                println("🖼️ Starting event banner upload flow for URI: $uri")
                val file = fileFromUri(uri)
                println("📁 Temporary file snapshot created: ${file.name}, size: ${file.length()} bytes")

                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                // 🔐 KEY MATCHING: "image" form key matching Ktor multi-part boundaries exactly
                val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)

                repository.uploadBanner(multipartBody).onEach { result ->
                    state = when (result) {
                        is Resource.Loading -> state.copy(isUploadingBanner = true, errorMessage = null)
                        is Resource.Success -> {
                            println("✅ Banner file uploaded successfully endpoint url: ${result.data}")
                            state.copy(isUploadingBanner = false, uploadedBannerUrl = result.data)
                        }
                        is Resource.Error -> {
                            println("❌ Repository upload failure trace logs: ${result.message}")
                            state.copy(isUploadingBanner = false, errorMessage = result.message)
                        }
                    }
                }.launchIn(this)

            } catch (e: Exception) {
                println("❌ Local stream parsing exception trace:")
                e.printStackTrace()
                state = state.copy(isUploadingBanner = false, errorMessage = "Failed to parse local media stream file resources.")
            }
        }
    }

    fun publishForm(
        title: String, club: String, date: String, time: String,
        venue: String, fee: String, description: String, category: String
    ) {
        viewModelScope.launch {
            // Validation block checks fields data correctness rules
            val validationErrors = mutableListOf<String>()
            if (title.isBlank()) validationErrors.add("Event Title")
            if (club.isBlank()) validationErrors.add("Organizing Committee")
            if (date.isBlank()) validationErrors.add("Event Date")
            if (time.isBlank()) validationErrors.add("Event Time")
            if (venue.isBlank()) validationErrors.add("Venue Address")
            if (description.isBlank()) validationErrors.add("Description details")
            if (state.uploadedBannerUrl.isNullOrBlank()) validationErrors.add("Banner Poster Image")

            if (validationErrors.isNotEmpty()) {
                val errorMsg = "Please populate missing fields: ${validationErrors.joinToString(", ")}"
                _event.emit(CreateEventUiEvent.ShowToast(errorMsg))
                return@launch
            }

            val parsedFee = if (fee.trim().replace("$", "").toDoubleOrNull() ?: 0.0 == 0.0) "Free" else fee.trim()

            repository.submitEvent(
                title = title.trim(), club = club.trim(), banner = state.uploadedBannerUrl!!,
                date = date.trim(), time = time.trim(), venue = venue.trim(),
                fee = parsedFee, description = description.trim(), category = category
            ).onEach { result ->
                state = when (result) {
                    is Resource.Loading -> state.copy(isPublishingEvent = true, errorMessage = null)
                    is Resource.Success -> state.copy(isPublishingEvent = false, executionSuccess = true)
                    is Resource.Error -> state.copy(isPublishingEvent = false, errorMessage = result.message)
                }
            }.launchIn(this)
        }
    }

    // Helper method to resolve content provider streams to sandboxed app caching directory
    private fun fileFromUri(uri: Uri): File {
        val inputStream = application.contentResolver.openInputStream(uri)
        val file = File.createTempFile(
            "event-banner-${System.currentTimeMillis()}-stream",
            ".jpg",
            application.cacheDir
        )
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    fun resetErrors() { state = state.copy(errorMessage = null) }

    sealed class CreateEventUiEvent {
        data class ShowToast(val msg: String) : CreateEventUiEvent()
    }
}