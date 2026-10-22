package com.example.dealerappnative.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.semantics.Role
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** No callback means no input handler, animation state or draw node. Standard clickable owns gestures. */
internal fun Modifier.softCardPress(enabled: Boolean = true, onClick: (() -> Unit)? = null): Modifier =
    if (!enabled || onClick == null) this else clickable(
        role = Role.Button, interactionSource = null, indication = SoftPressIndication, onClick = onClick
    )

/** Clickable creates this indication lazily on first interaction; idle cards allocate no animation. */
internal object SoftPressIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode = SoftPressNode(interactionSource)
    override fun equals(other: Any?): Boolean = other === this
    override fun hashCode(): Int = System.identityHashCode(this)
}

private class SoftPressNode(private val interactions: InteractionSource) : Modifier.Node(), DrawModifierNode {
    private val scale = Animatable(1f)

    override fun onAttach() {
        coroutineScope.launch {
            scale.snapTo(1f)
            interactions.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> scale.animateTo(.968f,
                        spring(dampingRatio = 1f, stiffness = 280f, visibilityThreshold = .0001f))
                    is PressInteraction.Release -> {
                        // Very short taps can emit Press/Release in the same frame. Show a small pulse
                        // only while the element remains attached; never delay its action/navigation.
                        if (scale.value > .995f) scale.animateTo(.985f, tween(75))
                        scale.animateTo(1f, spring(dampingRatio = 1f, stiffness = 280f, visibilityThreshold = .0001f))
                    }
                    is PressInteraction.Cancel -> scale.animateTo(1f,
                        spring(dampingRatio = 1f, stiffness = 400f, visibilityThreshold = .0001f))
                }
            }
        }
    }

    override fun onReset() {
        if (isAttached) coroutineScope.launch { scale.snapTo(1f) }
    }

    override fun ContentDrawScope.draw() {
        // Snapshot reads invalidate drawing only: no list recomposition, remeasurement or graphics layer.
        scale(scale.value, scale.value) { this@draw.drawContent() }
    }
}
