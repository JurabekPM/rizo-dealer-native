package com.example.dealerappnative.navigation

import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.dealerappnative.ui.components.TabletColors
import com.example.dealerappnative.ui.components.tabletPanel
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.ui.theme.DealerDesign

internal enum class DealerWindowLayout {
    Compact, Tablet, Split;

    val hasRail get() = this != Compact
    val hasPanes get() = this == Split
}

internal val LocalDealerWindowLayout = compositionLocalOf { DealerWindowLayout.Compact }

internal val LocalDealerSplitWidth = staticCompositionLocalOf<DealerSplitWidth> {
    error("DealerListDetail requires DealerAdaptiveShell in a split window")
}

@Stable
internal class DealerSplitWidth(private val preferences: SharedPreferences) {
    var fraction by mutableFloatStateOf(
        preferences.getFloat("split_list_fraction", .40f).let { if (it in 0f..1f) it else .40f }
    )
        private set

    fun resize(value: Float) {
        fraction = value
    }

    fun save() {
        preferences.edit().putFloat("split_list_fraction", fraction).apply()
    }
}

internal fun dealerWindowLayout(width: Dp, height: Dp): DealerWindowLayout = when {
    width < 600.dp || height < 480.dp -> DealerWindowLayout.Compact
    width >= 840.dp && width > height -> DealerWindowLayout.Split
    else -> DealerWindowLayout.Tablet
}

@Composable
internal fun DealerAdaptiveShell(
    rail: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val splitWidth = remember(context) {
        DealerSplitWidth(context.getSharedPreferences("dealer_layout", Context.MODE_PRIVATE))
    }
    BoxWithConstraints(Modifier.fillMaxSize().background(DealerDesign.surface)) {
        val keyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
        val windowHeight = if (keyboardVisible) LocalConfiguration.current.screenHeightDp.dp else maxHeight
        val mode = dealerWindowLayout(maxWidth, windowHeight)
        CompositionLocalProvider(
            LocalDealerWindowLayout provides mode,
            LocalDealerSplitWidth provides splitWidth
        ) {
            Row(Modifier.fillMaxSize()) {
                if (mode.hasRail) {
                    Box(Modifier.width(94.dp).fillMaxHeight().background(TabletColors.canvas).safeDrawingPadding()
                        .padding(start = 10.dp, end = 8.dp, top = 6.dp, bottom = 6.dp)
                        .tabletPanel(TabletColors.rail)) { rail() }
                }
                Box(Modifier.weight(1f).fillMaxHeight()) { content() }
            }
        }
    }
}

@Composable
internal fun DealerTabletContent(
    maxWidth: Dp = 600.dp,
    content: @Composable () -> Unit
) {
    val tablet = LocalDealerWindowLayout.current.hasRail
    Box(
        Modifier.fillMaxSize().then(if (tablet) Modifier.background(TabletColors.canvas).padding(horizontal = 6.dp, vertical = 6.dp) else Modifier),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(Modifier.then(if (tablet) Modifier.widthIn(max = maxWidth) else Modifier).fillMaxSize()) {
            content()
        }
    }
}

@Composable
internal fun <T : Enum<T>> DealerNavigationRail(
    destinations: List<T>,
    selected: T,
    unreadCounts: Map<T, Int>,
    title: (T) -> String,
    icon: (T) -> Int,
    onSelect: (T) -> Unit,
    iconColor: Color = DealerDesign.secondary,
    textStyle: TextStyle = DealerDesign.navigation,
    enabled: (T) -> Boolean = { true }
) {
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).selectableGroup()
        .padding(horizontal = 4.dp, vertical = 8.dp)) {
        destinations.forEach { destination ->
            val active = destination == selected
            val unreadCount = unreadCounts[destination] ?: 0
            Column(
                Modifier.fillMaxWidth().heightIn(min = 62.dp)
                    .background(if (active) DealerDesign.softAccent else Color.Transparent, RoundedCornerShape(13.dp))
                    .semantics { if (unreadCount > 0) stateDescription = "Новых: $unreadCount" }
                    .selectable(
                        selected = active,
                        enabled = enabled(destination),
                        role = Role.Tab,
                        interactionSource = null,
                        indication = null,
                        onClick = { onSelect(destination) }
                    )
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedNavigationIcon(icon(destination), active, iconColor, unreadCount)
                Text(
                    title(destination), Modifier.padding(top = 4.dp),
                    color = if (active) DealerDesign.accent else DealerDesign.tertiary,
                    style = textStyle,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
