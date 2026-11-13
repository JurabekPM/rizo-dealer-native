package com.example.dealerappnative

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.example.dealerappnative.navigation.DealerDestination
import com.example.dealerappnative.ui.theme.DealerPrototypeTheme
import com.example.dealerappnative.ui.theme.readThemeMode
import com.example.dealerappnative.ui.theme.saveThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Device checks can choose the initial administrator tab after role selection.
        val initialTab = intent.getIntExtra("previewTab", 0)
            .coerceIn(0, DealerDestination.entries.lastIndex)
        if (Build.VERSION.SDK_INT >= 29) window.decorView.isForceDarkAllowed = false
        val preferences = getSharedPreferences("dealer_appearance", Context.MODE_PRIVATE)
        setContent {
            var mode by remember { mutableStateOf(preferences.readThemeMode()) }
            val dark = mode.usesDarkTheme(isSystemInDarkTheme())
            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !dark
                    isAppearanceLightNavigationBars = !dark
                }
            }
            DealerPrototypeTheme(dark) {
                DealerEntry(initialTab, mode, onThemeChange = {
                    preferences.saveThemeMode(it)
                    mode = it
                })
            }
        }
    }
}
