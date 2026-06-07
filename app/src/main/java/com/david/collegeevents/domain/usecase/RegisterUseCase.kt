package com.david.collegeevents.domain.usecase

import com.david.collegeevents.domain.model.AuthResult
import com.david.collegeevents.domain.repository.AuthRepository
import com.david.collegeevents.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(name: String, enr: String, branch: String, email: String, pass: String): Flow<Resource<AuthResult>> {
        if (name.isBlank() || enr.isBlank() || branch.isBlank() || email.isBlank() || pass.isBlank()) {
            return flow { emit(Resource.Error("All fields are mandatory")) }
        }
        if (!email.contains("@") || !email.endsWith(".edu")) {
            return flow { emit(Resource.Error("Please use a valid University Email (.edu)")) }
        }
        return repository.register(name.trim(), enr.trim(), branch.trim(), email.trim(), pass)
    }
}