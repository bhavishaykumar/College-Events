package com.david.collegeevents.presentation.settings

import com.david.collegeevents.utils.ThemeConfig

data class SettingsState(
    val themeConfig: ThemeConfig = ThemeConfig.FOLLOW_SYSTEM
)