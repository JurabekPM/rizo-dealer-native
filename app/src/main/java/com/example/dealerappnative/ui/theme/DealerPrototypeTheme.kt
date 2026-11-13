package com.example.dealerappnative.ui.theme

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DealerPrototypeTheme(
    dark: Boolean = LocalDealerDarkTheme.current,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalDealerDarkTheme provides dark) {
        val base = if (dark) darkColorScheme() else lightColorScheme()
        val scheme = base.copy(
            primary = DealerDesign.accent,
            onPrimary = DealerDesign.onAccent,
            primaryContainer = if (dark) AgentDesign.tint else base.primaryContainer,
            onPrimaryContainer = if (dark) DealerDesign.text else base.onPrimaryContainer,
            background = DealerDesign.background,
            surface = DealerDesign.surface,
            onSurface = DealerDesign.text,
            onBackground = DealerDesign.text,
            onSurfaceVariant = if (dark) DealerDesign.secondary else base.onSurfaceVariant
        )
        PrototypeFontWeights {
            MaterialTheme(
                colorScheme = scheme,
                typography = Typography(
                    bodyLarge = DealerDesign.body,
                    bodyMedium = DealerDesign.body,
                    bodySmall = DealerDesign.detail
                )
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides scheme.onSurface,
                    LocalRippleConfiguration provides RippleConfiguration(
                        color = scheme.onSurface,
                        rippleAlpha = RippleAlpha(
                            draggedAlpha = .015f, focusedAlpha = .025f,
                            hoveredAlpha = 0f, pressedAlpha = .025f
                        )
                    ),
                    content = content
                )
            }
        }
    }
}
