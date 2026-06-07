package com.david.collegeevents.domain.usecase

import com.david.collegeevents.domain.model.AuthResult
import com.david.collegeevents.domain.repository.AuthRepository
import com.david.collegeevents.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(enrollment: String, pass: String): Flow<Resource<AuthResult>> {
        if (enrollment.isBlank() || pass.isBlank()) {
            return flow { emit(Resource.Error("Fields cannot be empty")) }
        }
        return repository.login(enrollment.trim(), pass)
    }
}