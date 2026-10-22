package com.example.dealerappnative.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

internal enum class AgentStorePart { Logo, Name, Debt, Id }

/** Only the Home store-list flow supplies this scope; other store screens stay unchanged. */
@OptIn(ExperimentalSharedTransitionApi::class)
internal class AgentStoreSharedTransition(
    private val sharedScope: SharedTransitionScope,
    private val visibilityScope: AnimatedVisibilityScope
) {
    @Composable
    fun modifier(storeId: String, part: AgentStorePart): Modifier = with(sharedScope) {
        val content = rememberSharedContentState("agent-store/$storeId/${part.name}")
        if (part == AgentStorePart.Logo) Modifier.sharedElement(
            sharedContentState = content, animatedVisibilityScope = visibilityScope,
            boundsTransform = { _, _ -> tween(360, easing = FastOutSlowInEasing) }
        ) else Modifier.sharedBounds(
            sharedContentState = content, animatedVisibilityScope = visibilityScope,
            boundsTransform = { _, _ -> tween(360, easing = FastOutSlowInEasing) },
            enter = fadeIn(tween(240)), exit = fadeOut(tween(180)),
            resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(ContentScale.Fit)
        )
    }
}

internal val LocalAgentStoreTransition = staticCompositionLocalOf<AgentStoreSharedTransition?> { null }

@Composable
internal fun Modifier.agentStoreSharedPart(storeId: String, part: AgentStorePart): Modifier {
    val transition = LocalAgentStoreTransition.current ?: return this
    return then(transition.modifier(storeId, part))
}
