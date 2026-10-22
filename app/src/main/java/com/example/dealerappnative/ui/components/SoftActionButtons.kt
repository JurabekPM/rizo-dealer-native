package com.example.dealerappnative.ui.components

import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton as MaterialIconButton
import androidx.compose.material3.TextButton as MaterialTextButton
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/** Keep Material sizing, semantics and click handling; replace only the press indication. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SoftIconButton(onClick: () -> Unit, modifier: Modifier = Modifier,
    enabled: Boolean = true, content: @Composable () -> Unit) {
    val interactions = remember { MutableInteractionSource() }
    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        MaterialIconButton(onClick = onClick, enabled = enabled, interactionSource = interactions,
            modifier = if (enabled) modifier.indication(interactions, SoftPressIndication) else modifier, content = content)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SoftTextButton(onClick: () -> Unit, modifier: Modifier = Modifier,
    enabled: Boolean = true, content: @Composable RowScope.() -> Unit) {
    val interactions = remember { MutableInteractionSource() }
    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        MaterialTextButton(onClick = onClick, enabled = enabled, interactionSource = interactions,
            modifier = if (enabled) modifier.indication(interactions, SoftPressIndication) else modifier, content = content)
    }
}
