package com.david.collegeevents.presentation.profile

import com.david.collegeevents.domain.model.UserProfile

data class ProfileState(
    val isLoading: Boolean = false,
    val profileData: UserProfile? = null,
    val error: String? = null,
    val isLoggedOut: Boolean = false
)