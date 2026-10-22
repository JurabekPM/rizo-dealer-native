package com.example.dealerappnative.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.AgentStoreDebtData
import com.example.dealerappnative.ui.theme.AgentDesign

/** The wide search slides out vertically; the toolbar action fades in at the same time. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AgentStoreDebtHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    state: TopAppBarState,
    searchFocus: FocusRequester,
    onBack: () -> Unit,
    toolbarSearchActive: Boolean,
    onSearchClose: () -> Unit,
    onSearchClick: () -> Unit
) {
    val density = LocalDensity.current
    val keyboard = LocalSoftwareKeyboardController.current
    val expandedSearchFocus = remember { FocusRequester() }
    LaunchedEffect(toolbarSearchActive) {
        if (toolbarSearchActive) {
            // Wait until Crossfade has attached the toolbar field before requesting focus.
            withFrameNanos { }
            searchFocus.requestFocus()
            keyboard?.show()
        }
    }
    var overviewHeightPx by remember { mutableIntStateOf(with(density) { 142.dp.roundToPx() }) }
    val overviewHeight = with(density) { overviewHeightPx.toDp() }
    SideEffect {
        state.heightOffsetLimit = -with(density) { (overviewHeight + 48.dp).toPx() }
    }
    val removedHeight = with(density) { (-state.heightOffset).toDp() }
    // Search responds to the first 48 dp of scrolling, rather than waiting for the summary.
    val searchProgress = (removedHeight / 48.dp).coerceIn(0f, 1f)
    val overviewRemoved = (removedHeight - 48.dp).coerceIn(0.dp, overviewHeight)
    val overviewProgress = (overviewRemoved / overviewHeight).coerceIn(0f, 1f)
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().height(56.dp).background(Color.White).padding(start = 4.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            SoftIconButton(onClick = if (toolbarSearchActive) onSearchClose else onBack, modifier = Modifier.size(48.dp)
                .semantics { contentDescription = if (toolbarSearchActive) "Закрыть поиск" else "Назад" }) {
                DealerIcon(R.drawable.ic_arrow_back, AgentDesign.text)
            }
            Crossfade(targetState = toolbarSearchActive, animationSpec = tween(180),
                label = "Debt toolbar search", modifier = Modifier.weight(1f)) { searching ->
                Box(Modifier.fillMaxWidth().height(40.dp), contentAlignment = Alignment.CenterStart) {
                    if (searching) DebtSearch(query, onQueryChange, searchFocus, showSearchIcon = false)
                    else Text("Задолженность магазинов", style = AgentDesign.screenTitle, color = AgentDesign.text,
                        maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(start = 4.dp))
                }
            }
            if (toolbarSearchActive) {
                SoftIconButton(onClick = {
                    onQueryChange("")
                    searchFocus.requestFocus()
                    keyboard?.show()
                }, modifier = Modifier.size(48.dp).semantics { contentDescription = "Очистить поиск" }) {
                    DealerIcon(R.drawable.ic_agent_clear, AgentDesign.secondary, AgentDesign.iconSize)
                }
            } else Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                if (searchProgress > 0f) {
                    SoftIconButton(onClick = onSearchClick, modifier = Modifier.size(48.dp)
                        .graphicsLayer {
                            alpha = searchProgress
                            scaleX = .8f + .2f * searchProgress
                            scaleY = scaleX
                        }.semantics { contentDescription = "Открыть поиск магазинов" }) {
                        DealerIcon(R.drawable.ic_search,
                            if (query.isEmpty()) AgentDesign.secondary else AgentDesign.accent, AgentDesign.iconSize)
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(overviewHeight - overviewRemoved).clipToBounds()) {
            Column(Modifier.fillMaxWidth().wrapContentHeight(Alignment.Top, unbounded = true)
                .onSizeChanged { overviewHeightPx = it.height }
                .graphicsLayer { alpha = 1f - overviewProgress }
                .padding(horizontal = 16.dp, vertical = 12.dp)) {
                DebtOverview()
            }
        }
        Box(Modifier.fillMaxWidth().height(48.dp * (1f - searchProgress)).clipToBounds()) {
            Box(Modifier.fillMaxWidth().wrapContentHeight(Alignment.Top, unbounded = true)
                .height(40.dp).padding(horizontal = 16.dp).offset(y = -40.dp * searchProgress)
                .graphicsLayer { alpha = 1f - searchProgress }
                .clip(RoundedCornerShape(AgentDesign.radius)).background(Color.White)) {
                if (!toolbarSearchActive) DebtSearch(query, onQueryChange, expandedSearchFocus)
            }
        }
    }
}

@Composable
private fun DebtOverview() {
    val data = AgentStoreDebtData
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(AgentDesign.radius)).background(Color.White).padding(16.dp)) {
        Text("Общая задолженность", style = AgentDesign.label, color = AgentDesign.secondary)
        Spacer(Modifier.height(4.dp))
        Text(data.totalText, style = AgentDesign.reportTitle, color = AgentDesign.negative)
        Spacer(Modifier.height(10.dp))
        Text("Всего ${data.stores.size} · С долгом ${data.withDebtCount} · Без долга ${data.withoutDebtCount}",
            style = AgentDesign.detail, color = AgentDesign.secondary)
    }
}

@Composable
private fun DebtSearch(query: String, onChange: (String) -> Unit, focus: FocusRequester, showSearchIcon: Boolean = true) {
    val keyboard = LocalSoftwareKeyboardController.current
    BasicTextField(query, onValueChange = onChange, singleLine = true,
        textStyle = AgentDesign.heroLabel.copy(color = AgentDesign.text), cursorBrush = SolidColor(AgentDesign.accent),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
        modifier = Modifier.fillMaxSize().focusRequester(focus).semantics { contentDescription = "Поиск магазинов" },
        decorationBox = { inner ->
            Row(Modifier.fillMaxSize().padding(horizontal = 11.dp), verticalAlignment = Alignment.CenterVertically) {
                if (showSearchIcon) {
                    DealerIcon(R.drawable.ic_search, AgentDesign.secondary, AgentDesign.iconSize)
                    Spacer(Modifier.width(8.dp))
                }
                Box(Modifier.weight(1f)) {
                    if (query.isEmpty()) Text("Поиск по названию или ID", style = AgentDesign.heroLabel,
                        color = AgentDesign.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    inner()
                }
            }
        })
}
