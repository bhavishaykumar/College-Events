package com.david.collegeevents.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class TokenManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role") // 👈 NEW ROLE KEY
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val userNameFlow: Flow<String?> = context.dataStore.data.map { it[USER_NAME_KEY] }

    // 🔴 NEW: Flow to read user role instantly anywhere in the UI
    val userRoleFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_ROLE_KEY] ?: "STUDENT" // Default is STUDENT if empty
    }

    // 🔴 Updated to accept and save 'role'
    suspend fun saveSession(token: String, userName: String, role: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_NAME_KEY] = userName
            preferences[USER_ROLE_KEY] = role.uppercase() // Enforce uppercase
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}