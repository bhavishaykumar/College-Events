package com.david.collegeevents.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("enrollmentNumber") val enrollmentNumber: String,
    @SerializedName("branchDepartment") val branchDepartment: String,
    @SerializedName("universityEmail") val universityEmail: String,
    @SerializedName("password") val password: String
)

data class LoginRequest(
    @SerializedName("enrollmentNumber") val enrollmentNumber: String,
    @SerializedName("password") val password: String
)

data class StudentProfileDto(
    @SerializedName("id") val id: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("enrollmentNumber") val enrollmentNumber: String,
    @SerializedName("branchDepartment") val branchDepartment: String,
    @SerializedName("universityEmail") val universityEmail: String
)

data class AuthResponseDto(
    @SerializedName("token") val token: String,
    @SerializedName("student") val student: StudentProfileDto
)

data class GenericErrorDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)