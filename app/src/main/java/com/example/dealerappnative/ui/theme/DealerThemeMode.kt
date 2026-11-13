package com.example.dealerappnative.ui.theme

import android.content.SharedPreferences

internal enum class DealerThemeMode(val label: String) {
    System("Как на телефоне"),
    Light("Светлая"),
    Dark("Тёмная");

    fun usesDarkTheme(systemDark: Boolean): Boolean = when (this) {
        System -> systemDark
        Light -> false
        Dark -> true
    }
}

internal fun SharedPreferences.readThemeMode(): DealerThemeMode {
    val saved = getString("mode", null)
    return DealerThemeMode.entries.firstOrNull { it.name == saved } ?: DealerThemeMode.System
}

internal fun SharedPreferences.saveThemeMode(mode: DealerThemeMode) {
    edit().putString("mode", mode.name).apply()
}
