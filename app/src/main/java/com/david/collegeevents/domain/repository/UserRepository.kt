package com.david.collegeevents.domain.repository

import com.david.collegeevents.domain.model.UserProfile
import com.david.collegeevents.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(): Flow<Resource<UserProfile>>
}