package com.example.dealerappnative

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.dealerappnative.ui.theme.DealerThemeMode
import com.example.dealerappnative.ui.theme.readThemeMode
import com.example.dealerappnative.ui.theme.saveThemeMode
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DealerThemeModeTest {
    @Test fun defaultsToPhoneAndPersistsManualOverrides() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "dealer_theme_test_${UUID.randomUUID()}"
        val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        try {
            val defaultMode = preferences.readThemeMode()
            assertEquals(DealerThemeMode.System, defaultMode)
            assertFalse(defaultMode.usesDarkTheme(systemDark = false))
            assertTrue(defaultMode.usesDarkTheme(systemDark = true))

            preferences.saveThemeMode(DealerThemeMode.Dark)
            assertEquals(DealerThemeMode.Dark, preferences.readThemeMode())
            assertTrue(preferences.readThemeMode().usesDarkTheme(systemDark = false))

            preferences.saveThemeMode(DealerThemeMode.Light)
            assertEquals(DealerThemeMode.Light, preferences.readThemeMode())
            assertFalse(preferences.readThemeMode().usesDarkTheme(systemDark = true))

            preferences.saveThemeMode(DealerThemeMode.System)
            assertEquals(DealerThemeMode.System, preferences.readThemeMode())
        } finally {
            context.deleteSharedPreferences(name)
        }
    }
}
