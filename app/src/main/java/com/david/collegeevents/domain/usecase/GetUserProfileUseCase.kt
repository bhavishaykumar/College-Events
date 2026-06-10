package com.david.collegeevents.domain.usecase

import com.david.collegeevents.domain.model.UserProfile
import com.david.collegeevents.domain.repository.UserRepository
import com.david.collegeevents.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<Resource<UserProfile>> = repository.getUserProfile()
}