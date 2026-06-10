package com.david.collegeevents.presentation.auth

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val studentName: String = "",
    val userRole: String = "STUDENT"
)