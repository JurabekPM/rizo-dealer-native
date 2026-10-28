package com.example.dealerappnative.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.ProfileDesign as Design

/** Same toolbar search handoff as Stores; the tabs are a separate pinned sibling. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AdminPeopleAppBar(query: String, onQueryChange: (String) -> Unit, state: TopAppBarState,
    searching: Boolean, onSearch: () -> Unit, onCloseSearch: () -> Unit, onBack: () -> Unit) {
    val focus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(searching) {
        if (searching) { withFrameNanos { }; focus.requestFocus(); keyboard?.show() }
    }
    val density = LocalDensity.current
    val progress = (-state.heightOffset / with(density) { 48.dp.toPx() }).coerceIn(0f, 1f)
    Row(Modifier.fillMaxWidth().height(56.dp).background(Color.White).padding(start = 4.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        SoftIconButton(if (searching) onCloseSearch else onBack, Modifier.size(48.dp)
            .semantics { contentDescription = if (searching) "Закрыть поиск" else "Назад" }) { DealerIcon(R.drawable.ic_arrow_back, Design.text) }
        Crossfade(searching, animationSpec = tween(180), label = "People toolbar search", modifier = Modifier.weight(1f)) { active ->
            Box(Modifier.fillMaxWidth().height(40.dp), contentAlignment = Alignment.CenterStart) {
                if (active) BasicTextField(query, onQueryChange, singleLine = true, textStyle = Design.value.copy(color = Design.text),
                    cursorBrush = SolidColor(DealerDesign.accent), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
                    modifier = Modifier.fillMaxWidth().focusRequester(focus).semantics { contentDescription = "Поиск сотрудников" },
                    decorationBox = { inner ->
                        Box(Modifier.fillMaxWidth()) {
                            if (query.isEmpty()) Text("Поиск сотрудников", style = Design.value, color = Design.secondary)
                            inner()
                        }
                    })
                else Text("Профиль", style = DealerDesign.title, color = Design.text, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 4.dp))
            }
        }
        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            if (searching) SoftIconButton({ onQueryChange(""); focus.requestFocus(); keyboard?.show() },
                Modifier.size(48.dp).semantics { contentDescription = "Очистить поиск" }) { DealerIcon(R.drawable.ic_agent_clear, Design.secondary, 22.dp) }
            else if (progress > 0f) SoftIconButton(onSearch, Modifier.size(48.dp).graphicsLayer {
                alpha = progress; scaleX = .8f + .2f * progress; scaleY = scaleX
            }.semantics { contentDescription = "Открыть поиск сотрудников" }) {
                DealerIcon(R.drawable.ic_search, if (query.isBlank()) Design.secondary else DealerDesign.accent, 22.dp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AdminPeopleControls(query: String, onQueryChange: (String) -> Unit, count: Int,
    state: TopAppBarState, searching: Boolean, onAdd: () -> Unit) {
    val density = LocalDensity.current
    val height = 92.dp
    SideEffect { state.heightOffsetLimit = -with(density) { height.toPx() } }
    val removed = with(density) { (-state.heightOffset).toDp() }.coerceIn(0.dp, height)
    // Hide search first. On a reverse drag the short count/add row comes back first,
    // while the toolbar search icon remains available. Total height follows the same offset.
    val searchRemoved = removed.coerceIn(0.dp, 48.dp)
    val actionsRemoved = (removed - 48.dp).coerceIn(0.dp, 44.dp)
    val searchProgress = searchRemoved / 48.dp
    val actionsProgress = actionsRemoved / 44.dp
    Column(Modifier.fillMaxWidth().background(Color.White)) {
        Box(Modifier.fillMaxWidth().height(48.dp - searchRemoved).clipToBounds()) {
            Box(Modifier.fillMaxWidth().wrapContentHeight(Alignment.Top, unbounded = true).height(48.dp)
                .graphicsLayer { alpha = 1f - searchProgress; translationY = -12.dp.toPx() * searchProgress }
                .padding(horizontal = 16.dp).padding(bottom = 10.dp)) {
                if (!searching) SearchField(query, onQueryChange, "Поиск сотрудников", height = 38.dp, textStyle = Design.value)
            }
        }
        Box(Modifier.fillMaxWidth().height(44.dp - actionsRemoved).clipToBounds()) {
            Row(Modifier.fillMaxWidth().wrapContentHeight(Alignment.Top, unbounded = true).height(44.dp)
                .graphicsLayer { alpha = 1f - actionsProgress; translationY = -6.dp.toPx() * actionsProgress }
                .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Сотрудники · $count", style = Design.label, color = Design.secondary, modifier = Modifier.weight(1f))
                Row(Modifier.height(40.dp).clip(RoundedCornerShape(10.dp)).softCardPress(onClick = onAdd)
                    .semantics { contentDescription = "Добавить сотрудника" }.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DealerIcon(R.drawable.ic_request_add, DealerDesign.accent, 18.dp)
                    Text("Добавить", style = Design.body, color = DealerDesign.accent)
                }
            }
        }
    }
}
