package com.example.dealerappnative.ui.components

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

internal data class PhotoZoom(val scale: Float = 1f, val offset: Offset = Offset.Zero) {
    fun around(frame: Rect, bitmap: ImageBitmap, focus: Offset, nextScale: Float, pan: Offset = Offset.Zero): PhotoZoom {
        val fitted = photoRect(bitmap, frame)
        val pivot = focus - frame.center
        val translated = pivot + (offset - pivot) * (nextScale / scale) + pan
        val maxX = ((fitted.width * nextScale - frame.width) / 2f).coerceAtLeast(0f)
        val maxY = ((fitted.height * nextScale - frame.height) / 2f).coerceAtLeast(0f)
        return PhotoZoom(nextScale, Offset(translated.x.coerceIn(-maxX, maxX), translated.y.coerceIn(-maxY, maxY)))
    }
}

@Composable
internal fun ZoomablePhoto(
    bitmap: ImageBitmap,
    label: String,
    enabled: Boolean,
    zoom: () -> PhotoZoom,
    onZoom: (PhotoZoom) -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: (Boolean) -> Unit,
    drawPhoto: DrawScope.(Rect) -> Unit
) {
    val currentBitmap by rememberUpdatedState(bitmap)
    val currentZoom by rememberUpdatedState(zoom)
    val updateZoom by rememberUpdatedState(onZoom)
    val updateDrag by rememberUpdatedState(onDrag)
    val finishDrag by rememberUpdatedState(onDragEnd)
    val scope = rememberCoroutineScope()
    var zoomAnimation by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(enabled) {
        if (!enabled) {
            zoomAnimation?.cancel()
            updateZoom(PhotoZoom())
        }
    }

    Canvas(
        Modifier.fillMaxSize()
            .semantics {
                contentDescription = label
                if (enabled) stateDescription = "${(zoom().scale * 100).roundToInt()}%"
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                var lastTapTime: Long? = null
                var lastTapPosition = Offset.Zero
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var pinching = false
                    var dragging = false
                    var distance = 0f
                    var dragStart = 0f
                    var velocity = 0f
                    val tracker = VelocityTracker()
                    var released = false
                    var tap = true
                    val frame = size.frame()
                    val slop = viewConfiguration.touchSlop
                    tracker.addPosition(down.uptimeMillis, down.position)
                    try {
                        do {
                            val event = awaitPointerEvent()
                            if (event.changes.any { it.isConsumed }) {
                                tap = false
                                break
                            }
                            val previous = currentZoom()
                            pinching = pinching || event.changes.count { it.pressed } > 1
                            val finger = event.changes.firstOrNull { it.id == down.id }
                            val delta = finger?.let { it.position - down.position } ?: Offset.Zero
                            finger?.let { tracker.addPosition(it.uptimeMillis, it.position) }
                            tap = tap && !pinching && delta.getDistance() <= viewConfiguration.touchSlop
                            if (pinching && dragging) {
                                dragging = false
                                finishDrag(false)
                            }
                            when {
                                pinching || (previous.scale > 1f && !tap) -> {
                                    zoomAnimation?.cancel()
                                    val scale = (previous.scale * event.calculateZoom()).coerceIn(1f, 4f)
                                    val center = event.calculateCentroid(useCurrent = false)
                                    updateZoom(previous.around(frame, currentBitmap,
                                        if (center.isSpecified) center else frame.center, scale, event.calculatePan()))
                                    event.changes.forEach { if (it.position != it.previousPosition) it.consume() }
                                }
                                dragging || (abs(delta.y) > slop && abs(delta.y) > abs(delta.x) * 1.2f) -> {
                                    if (!dragging) {
                                        zoomAnimation?.cancel()
                                        dragStart = if (delta.y < 0f) -slop else slop
                                        dragging = true
                                    }
                                    distance = delta.y - dragStart
                                    updateDrag(distance)
                                    if (finger?.pressed == false) velocity = tracker.calculateVelocity().y
                                    finger?.consume()
                                }
                            }
                            released = event.changes.none { it.pressed }
                            if (released && finger != null && tap && finger.uptimeMillis - down.uptimeMillis < viewConfiguration.longPressTimeoutMillis) {
                                val interval = lastTapTime?.let { down.uptimeMillis - it }
                                if (interval != null &&
                                    interval in viewConfiguration.doubleTapMinTimeMillis..viewConfiguration.doubleTapTimeoutMillis &&
                                    (finger.position - lastTapPosition).getDistance() <= 48.dp.toPx()) {
                                    zoomAnimation?.cancel()
                                    lastTapTime = null
                                    val start = currentZoom()
                                    val target = if (start.scale > 1.01f) PhotoZoom() else
                                        start.around(frame, currentBitmap, finger.position, 2.5f)
                                    finger.consume()
                                    zoomAnimation = scope.launch {
                                        animate(0f, 1f, animationSpec = tween(240)) { fraction, _ ->
                                            updateZoom(PhotoZoom(start.scale + (target.scale - start.scale) * fraction,
                                                lerp(start.offset, target.offset, fraction)))
                                        }
                                    }
                                } else {
                                    lastTapTime = finger.uptimeMillis
                                    lastTapPosition = finger.position
                                }
                            }
                        } while (!released)
                    } finally {
                        if (!tap || !released) lastTapTime = null
                        if (dragging) {
                            val farEnough = abs(distance) >= minOf(96.dp.toPx(), frame.height * .22f)
                            val flick = abs(distance) >= 28.dp.toPx() && abs(velocity) >= 850.dp.toPx() &&
                                velocity * distance > 0f
                            finishDrag(released && (farEnough || flick))
                        }
                    }
                }
            }
    ) { drawPhoto(Rect(Offset.Zero, size)) }
}

private fun IntSize.frame() = Rect(Offset.Zero, Size(width.toFloat(), height.toFloat()))
