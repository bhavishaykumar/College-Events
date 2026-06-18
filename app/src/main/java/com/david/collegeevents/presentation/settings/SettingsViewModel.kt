package com.david.collegeevents.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.david.collegeevents.utils.ThemeConfig
import com.david.collegeevents.utils.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeManager: ThemeManager
) : ViewModel() {

    var state by mutableStateOf(SettingsState())
        private set

    init {
        themeManager.themeFlow.onEach { themeConfig ->
            state = state.copy(themeConfig = themeConfig)
        }.launchIn(viewModelScope)
    }

    fun onThemeSelected(themeConfig: ThemeConfig) {
        viewModelScope.launch {
            themeManager.saveTheme(themeConfig)
        }
    }
}