package com.example.dealerappnative.ui.theme

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DealerPrototypeTheme(content: @Composable () -> Unit) {
    PrototypeFontWeights {
        MaterialTheme(
            typography = Typography(
                bodyLarge = DealerDesign.body,
                bodyMedium = DealerDesign.body,
                bodySmall = DealerDesign.detail
            ),
            colorScheme = lightColorScheme(
                primary = DealerDesign.accent,
                background = DealerDesign.background,
                surface = Color.White,
                onSurface = DealerDesign.text,
                onBackground = DealerDesign.text
            ),
            content = {
                CompositionLocalProvider(LocalRippleConfiguration provides RippleConfiguration(
                    color = Color.Black,
                    rippleAlpha = RippleAlpha(draggedAlpha = .015f, focusedAlpha = .025f,
                        hoveredAlpha = 0f, pressedAlpha = .025f)
                )) { content() }
            }
        )
    }
}
