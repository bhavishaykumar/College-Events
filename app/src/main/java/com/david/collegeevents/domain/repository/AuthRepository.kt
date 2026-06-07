package com.david.collegeevents.domain.repository


import com.david.collegeevents.domain.model.AuthResult
import com.david.collegeevents.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(enrollment: String, pass: String): Flow<Resource<AuthResult>>
    fun register(name: String, enr: String, branch: String, email: String, pass: String): Flow<Resource<AuthResult>>
}