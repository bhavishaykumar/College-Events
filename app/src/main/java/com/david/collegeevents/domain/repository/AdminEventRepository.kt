package com.david.collegeevents.domain.repository

import com.david.collegeevents.utils.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface AdminEventRepository {
    fun uploadBanner(imagePart: MultipartBody.Part): Flow<Resource<String>>
    fun submitEvent(
        title: String, club: String, banner: String, date: String,
        time: String, venue: String, fee: String, description: String, category: String
    ): Flow<Resource<String>>
}