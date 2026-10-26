package com.example.dealerappnative.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import com.example.dealerappnative.model.VisitResult

internal enum class VisitStorePart { Logo, Name, Debt }

/** One animation host is retained across list/detail navigation in either role. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun VisitResultsTransition(
    selectedId: String?, onSelect: (String) -> Unit,
    content: @Composable (String?, (String) -> Unit) -> Unit
) {
    var preparedId by remember { mutableStateOf(selectedId) }
    var opening by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val latestContent by rememberUpdatedState(content)
    val latestSelection by rememberUpdatedState(onSelect)
    val openVisit: (String) -> Unit = { id ->
        if (!opening && selectedId == null) {
            opening = true
            preparedId = id
            scope.launch {
                // Move the existing list into the animation host and capture the tapped card first.
                withFrameNanos { }
                withFrameNanos { }
                latestSelection(id)
                opening = false
            }
        }
    }
    val latestOpen by rememberUpdatedState(openVisit)
    // Moving this content preserves its identity when the lookahead host is added/removed.
    val listContent = remember {
        movableContentOf<VisitResultTransitionScope?> { transition ->
            CompositionLocalProvider(LocalVisitResultTransition provides transition) {
                latestContent(null, latestOpen)
            }
        }
    }
    if (preparedId == null && selectedId == null) {
        listContent(null) // Idle/first-entry scrolling has no SharedTransitionLayout or lookahead pass.
    } else SharedTransitionLayout(Modifier.fillMaxSize()) {
        val sharedScope = this
        val screenTransition = updateTransition(selectedId, label = "Visit navigation")
        LaunchedEffect(selectedId, opening) {
            if (selectedId == null && !opening) {
                // Bounds use 440 ms; release the host after a buffer even if shared state stays active.
                delay(VisitMotionDuration + 160L)
                preparedId = null
            }
        }
        screenTransition.AnimatedContent(modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                (fadeIn(tween(240, 80)) togetherWith fadeOut(tween(160))).using(SizeTransform(clip = false))
            }) { targetId ->
            val transition = remember(sharedScope, this, preparedId) { VisitResultTransitionScope(sharedScope, this, preparedId) }
            CompositionLocalProvider(LocalVisitResultTransition provides transition) {
                if (targetId == null) listContent(transition) else latestContent(targetId, latestOpen)
            }
        }
    }
}

private const val VisitMotionDuration = 440
private val VisitMotionEasing = CubicBezierEasing(.2f, 0f, 0f, 1f)

@OptIn(ExperimentalSharedTransitionApi::class)
private class VisitResultTransitionScope(
    private val sharedScope: SharedTransitionScope, private val visibilityScope: AnimatedVisibilityScope,
    private val activeStoreId: String?
) {
    @Composable
    fun storeModifier(storeId: String, part: VisitStorePart): Modifier {
        if (storeId != activeStoreId) return Modifier
        return with(sharedScope) {
            val state = rememberSharedContentState("visit-store/$storeId/${part.name}")
            if (part == VisitStorePart.Logo) Modifier.sharedElement(state,
                animatedVisibilityScope = visibilityScope,
                boundsTransform = { _, _ -> tween(VisitMotionDuration, easing = VisitMotionEasing) })
            else Modifier.sharedBounds(state, animatedVisibilityScope = visibilityScope,
                boundsTransform = { _, _ -> tween(VisitMotionDuration, easing = VisitMotionEasing) },
                resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(ContentScale.Fit, Alignment.CenterStart),
                enter = fadeIn(tween(240)), exit = fadeOut(tween(180)))
        }
    }

    @Composable
    fun modifier(storeId: String, result: VisitResult, layer: Int): Modifier {
        if (storeId != activeStoreId) return Modifier
        return with(sharedScope) {
        Modifier.sharedElement(rememberSharedContentState("visit-result/$storeId/${result.name}"),
            animatedVisibilityScope = visibilityScope,
            boundsTransform = { _, _ -> tween(VisitMotionDuration, easing = VisitMotionEasing) },
            zIndexInOverlay = layer + 1f)
        }
    }
}

private val LocalVisitResultTransition = staticCompositionLocalOf<VisitResultTransitionScope?> { null }

@Composable
internal fun Modifier.sharedVisitResult(storeId: String?, result: VisitResult, layer: Int): Modifier {
    val transition = LocalVisitResultTransition.current
    return if (storeId == null || transition == null) this else then(transition.modifier(storeId, result, layer))
}

@Composable
internal fun Modifier.sharedVisitStorePart(storeId: String, part: VisitStorePart): Modifier {
    val transition = LocalVisitResultTransition.current ?: return this
    return then(transition.storeModifier(storeId, part))
}
