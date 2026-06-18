package com.david.collegeevents.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeConfig {
    FOLLOW_SYSTEM, LIGHT, DARK
}

class ThemeManager(private val context: Context) {

    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_config")
    }

    val themeFlow: Flow<ThemeConfig> = context.dataStore.data.map { preferences ->
        val themeString = preferences[THEME_KEY] ?: ThemeConfig.FOLLOW_SYSTEM.name
        try {
            ThemeConfig.valueOf(themeString)
        } catch (e: Exception) {
            ThemeConfig.FOLLOW_SYSTEM
        }
    }

    suspend fun saveTheme(themeConfig: ThemeConfig) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeConfig.name
        }
    }
}