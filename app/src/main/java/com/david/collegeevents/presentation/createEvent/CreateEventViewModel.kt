package com.david.collegeevents.presentation.createEvent

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.david.collegeevents.domain.repository.AdminEventRepository
import com.david.collegeevents.domain.repository.EventDetailsRepository
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
    private val eventDetailsRepository: EventDetailsRepository,
    private val application: Application
) : ViewModel() {

    var state by mutableStateOf(CreateEventState())
        private set

    private val _event = MutableSharedFlow<CreateEventUiEvent>()
    val event = _event.asSharedFlow()

    private var originalImageUrl: String? = null // Keeps track of garbage cleanup reference pointer

    fun uploadBannerImage(uri: Uri) {
        viewModelScope.launch {
            try {
                val file = fileFromUri(uri)
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val multipartBody =
                    MultipartBody.Part.createFormData("image", file.name, requestBody)

                repository.uploadBanner(multipartBody).onEach { result ->
                    state = when (result) {
                        is Resource.Loading -> state.copy(
                            isUploadingBanner = true,
                            errorMessage = null
                        )

                        is Resource.Success -> {
                            // 🔴 GARBAGE CLEANUP TRIGGER: If user changes image during EDIT mode, purge old one first
                            originalImageUrl?.let { oldUrl ->
                                repository.deleteImage(oldUrl).launchIn(viewModelScope)
                            }
                            state.copy(isUploadingBanner = false, uploadedBannerUrl = result.data)
                        }

                        is Resource.Error -> state.copy(
                            isUploadingBanner = false,
                            errorMessage = result.message
                        )
                    }
                }.launchIn(this)
            } catch (e: Exception) {
                state = state.copy(
                    isUploadingBanner = false,
                    errorMessage = "Failed to parse local image stream."
                )
            }
        }
    }

    fun populateFieldsForEdit(existingEventId: String) {
        viewModelScope.launch {
            eventDetailsRepository.getEventDetail(existingEventId).onEach { result ->
                state = when (result) {
                    is Resource.Loading -> state.copy(isLoadingEvent = true)
                    is Resource.Success -> {
                        val event = result.data!!
                        originalImageUrl = event.bannerUrl
                        state.copy(
                            isLoadingEvent = false,
                            uploadedBannerUrl = event.bannerUrl,
                            prefillTitle = event.title,
                            prefillClub = event.clubName,
                            prefillDate = event.date,
                            prefillTime = event.time,
                            prefillVenue = event.venue,
                            prefillFee = event.registrationFee,
                            prefillDescription = event.description
                        )
                    }
                    is Resource.Error -> state.copy(
                        isLoadingEvent = false,
                        errorMessage = result.message
                    )
                }
            }.launchIn(this)
        }
    }

    fun saveOrUpdateForm(
        eventId: String?, title: String, club: String, date: String, time: String,
        venue: String, fee: String, description: String, category: String
    ) {
        viewModelScope.launch {
            if (title.isBlank() || club.isBlank() || date.isBlank() || time.isBlank() || venue.isBlank() || description.isBlank()) {
                _event.emit(CreateEventUiEvent.ShowToast("Please populate all mandatory metadata parameters fields."))
                return@launch
            }
            if (state.uploadedBannerUrl.isNullOrBlank()) {
                _event.emit(CreateEventUiEvent.ShowToast("Please upload an event banner poster image asset."))
                return@launch
            }

            val parsedFee = if (fee.trim().replace("$", "")
                    .toDoubleOrNull() ?: 0.0 == 0.0
            ) "Free" else fee.trim()

            val flowResult = if (eventId == null) {
                // Form entry CREATE path
                repository.submitEvent(
                    title.trim(),
                    club.trim(),
                    state.uploadedBannerUrl!!,
                    date.trim(),
                    time.trim(),
                    venue.trim(),
                    parsedFee,
                    description.trim(),
                    category
                )
            } else {
                // Form entry UPDATE path
                repository.modifyEvent(
                    eventId,
                    title.trim(),
                    club.trim(),
                    state.uploadedBannerUrl!!,
                    date.trim(),
                    time.trim(),
                    venue.trim(),
                    parsedFee,
                    description.trim(),
                    category
                )
            }

            flowResult.onEach { result ->
                state = when (result) {
                    is Resource.Loading -> state.copy(isPublishingEvent = true, errorMessage = null)
                    is Resource.Success -> state.copy(
                        isPublishingEvent = false,
                        executionSuccess = true
                    )

                    is Resource.Error -> state.copy(
                        isPublishingEvent = false,
                        errorMessage = result.message
                    )
                }
            }.launchIn(this)
        }
    }

    private fun fileFromUri(uri: Uri): File {
        val inputStream = application.contentResolver.openInputStream(uri)
        val file = File.createTempFile(
            "banner_form_cache",
            ".jpg",
            application.cacheDir
        )
        inputStream?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
        return file
    }

    fun resetErrors() {
        state = state.copy(errorMessage = null)
    }

    sealed class CreateEventUiEvent {
        data class ShowToast(val msg: String) : CreateEventUiEvent()
    }
}