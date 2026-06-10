package com.david.collegeevents.data.repository

import com.david.collegeevents.data.remote.ApiServices
import com.david.collegeevents.data.remote.dto.GenericErrorDto
import com.david.collegeevents.data.remote.dto.LoginRequest
import com.david.collegeevents.data.remote.dto.RegisterRequest
import com.david.collegeevents.domain.model.AuthResult
import com.david.collegeevents.domain.repository.AuthRepository
import com.david.collegeevents.utils.Resource
import com.david.collegeevents.utils.TokenManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: ApiServices,
    private val tokenManager: TokenManager // 👈 TokenManager inject kiya
) : AuthRepository {

    override fun login(enrollment: String, pass: String): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.login(LoginRequest(enrollment, pass))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                // 🔴 EDGE CASE FIXED: Save to local session permanently before emitting success
                tokenManager.saveSession(
                    token = body.token,
                    userName = body.student.fullName,
                    role = body.student.role
                )

                emit(
                    Resource.Success(
                        AuthResult(
                            token = body.token,
                            userName = body.student.fullName,
                            role = body.student.role
                        )
                    )
                )
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string())
                emit(Resource.Error(errorMsg))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    override fun register(
        name: String,
        enr: String,
        branch: String,
        email: String,
        pass: String
    ): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.register(RegisterRequest(name, enr, branch, email, pass))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                // 🔴 EDGE CASE FIXED: Save session immediately on registration
                tokenManager.saveSession(
                    token = body.token,
                    userName = body.student.fullName,
                    role = body.student.role
                )

                emit(
                    Resource.Success(
                        AuthResult(
                            token = body.token,
                            userName = body.student.fullName,
                            role = body.student.role
                        )
                    )
                )
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string())
                emit(Resource.Error(errorMsg))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Server offline. Please try again later."))
        }
    }

    private fun parseErrorMessage(json: String?): String {
        return try {
            Gson().fromJson(json, GenericErrorDto::class.java).message
        } catch (e: Exception) {
            "An unknown error occurred"
        }
    }
}