package com.example.dealerappnative.navigation

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.VectorGroup
import androidx.compose.ui.graphics.vector.VectorPath
import com.example.dealerappnative.R
import kotlin.math.PI
import kotlin.math.sin

/** Draws the original vectors in their own viewport. Split contours let parts move independently. */
internal class NavigationIconArtwork(@param:DrawableRes private val iconRes: Int, art: ImageVector) {
    private val viewport = art.viewportWidth
    private val group = art.root.single() as VectorGroup
    private val parts = group.flatMap { child ->
        val vector = child as VectorPath
        val contours = mutableListOf<MutableList<PathNode>>()
        vector.pathData.forEach { node ->
            if (node is PathNode.MoveTo || contours.isEmpty()) contours.add(mutableListOf())
            contours.last().add(node)
        }
        val stroke = if (vector.stroke == null) null else Stroke(
            width = vector.strokeLineWidth,
            cap = vector.strokeLineCap,
            join = vector.strokeLineJoin
        )
        contours.map { nodes ->
            val path = PathParser().addPathNodes(nodes).toPath().apply {
                translate(Offset(group.translationX, group.translationY))
            }
            Part(path, path.getBounds().center, stroke)
        }
    }
    private val mainShape = Path().apply { parts.forEach { addPath(it.path) } }
    private val route = if (iconRes == R.drawable.ic_visit_route) {
        PathMeasure().apply {
            setPath(Path().apply {
                moveTo(6f, 18f)
                lineTo(17f, 18f)
                cubicTo(21f, 18f, 21f, 12f, 17f, 12f)
                lineTo(7f, 12f)
                cubicTo(3f, 12f, 3f, 6f, 7f, 6f)
                lineTo(18f, 6f)
            }, forceClosed = false)
        }
    } else null
    private val clipboardPanel = if (iconRes == R.drawable.ic_visit) {
        Path().apply {
            moveTo(5f, 5f)
            lineTo(19f, 5f)
            lineTo(19f, 11f)
            cubicTo(14.3f, 11f, 11f, 14.7f, 11f, 19f)
            lineTo(5f, 19f)
            close()
        }
    } else null
    private val plus = if (iconRes == R.drawable.ic_visit) compound(6, 7) else null
    private val clipboard = if (iconRes == R.drawable.ic_visit) compound(0, 5) else null

    fun DrawScope.drawIcon(progress: Float, tint: Color, cutout: Color) {
        val center = Offset(viewport / 2f, viewport / 2f)
        val unit = size.width / viewport
        scale(unit, unit, pivot = Offset.Zero) {
            when (iconRes) {
                R.drawable.ic_visit_route -> drawRoute(progress, tint, cutout)
                R.drawable.ic_visit -> drawRequest(progress, tint, cutout)
                R.drawable.ic_box -> drawParcel(progress, tint, cutout, center)
                R.drawable.ic_money -> {
                    val turn = wave(phase(progress, .04f, .90f))
                    rotate(17f * turn, center) {
                        scale(1f - .70f * turn, 1f + .09f * turn, center) {
                            drawPath(mainShape, tint)
                        }
                    }
                }
                R.drawable.ic_store -> drawStore(progress, tint, cutout)
                R.drawable.ic_agent_statistics -> drawStatistics(progress, tint, cutout)
                R.drawable.ic_agent_history -> {
                    rotate(-62f * wave(phase(progress, 0f, .94f)), center) { drawPart(0, tint) }
                    rotate(145f * wave(phase(progress, .16f, 1f)), center) { drawPart(1, tint) }
                }
                R.drawable.ic_menu, R.drawable.ic_agent_menu -> drawMenu(progress, tint)
            }
        }
    }

    private fun DrawScope.drawRoute(progress: Float, tint: Color, cutout: Color) {
        val trace = phase(progress, .04f, .91f)
        drawPath(mainShape, tint)
        growFill(2, phase(progress, .02f, .38f), tint)
        growFill(1, phase(progress, .68f, 1f), tint)
        val measure = checkNotNull(route)
        if (trace > 0f && trace < 1f) {
            drawCircle(cutout, radius = .82f, center = measure.getPosition(trace * measure.length),
                alpha = .95f * wave(trace))
        }
    }

    private fun DrawScope.drawRequest(progress: Float, tint: Color, cutout: Color) {
        val fill = phase(progress, .11f, .87f)
        drawPath(checkNotNull(clipboard), tint)
        clipRect(left = 0f, top = 0f, right = viewport, bottom = 5f + 14f * fill) {
            drawPath(checkNotNull(clipboardPanel), tint)
        }
        // The original three lines become cutouts as the solid clipboard passes behind them.
        for (index in 2..4) {
            val line = parts[index]
            drawPart(index, tint)
            clipRect(left = 0f, top = 0f, right = viewport, bottom = 5f + 14f * fill) {
                drawPath(line.path, cutout)
            }
        }
        val plusPulse = wave(phase(progress, .20f, .99f))
        rotate(68f * plusPulse, parts[6].center) {
            scale(1f - .24f * plusPulse, 1f - .24f * plusPulse, parts[6].center) {
                drawPath(checkNotNull(plus), tint)
            }
        }
    }

    private fun DrawScope.drawParcel(progress: Float, tint: Color, cutout: Color, center: Offset) {
        val turn = wave(phase(progress, 0f, .94f))
        rotate(-19f * turn, center) {
            scale(1f - .24f * turn, 1f + .03f * turn, center) {
                drawPath(mainShape, tint)
                growFill(2, phase(progress, .13f, .58f), tint)
                growFill(0, phase(progress, .25f, .84f), lerp(tint, cutout, .12f))
                growFill(1, phase(progress, .36f, .96f), lerp(tint, cutout, .27f))
            }
        }
    }

    private fun DrawScope.drawStore(progress: Float, tint: Color, cutout: Color) {
        val swing = wave(phase(progress, 0f, .83f))
        translate(top = -2.4f * swing) {
            scale(1f + .08f * swing, 1f, parts[0].center) { drawPart(0, tint) }
        }
        drawPath(checkNotNull(storeBody), tint)
        growFill(3, phase(progress, .10f, .58f), lerp(tint, cutout, .20f))
        growFill(2, phase(progress, .38f, .95f), tint)
    }

    private fun DrawScope.drawStatistics(progress: Float, tint: Color, cutout: Color) {
        val fill = phase(progress, .14f, .92f)
        drawPath(statsFrame, tint)
        val fillEdge = 19f - fill * 14f
        clipRect(left = 0f, top = fillEdge, right = viewport, bottom = viewport) {
            drawPart(4, tint)
        }
        for (index in 0..2) {
            val barProgress = phase(progress, .04f + index * .095f, .84f + index * .055f)
            val pivot = Offset(parts[index].center.x, 17f)
            scale(1f, 1f - .67f * wave(barProgress), pivot) {
                drawPart(index, tint)
            }
            clipRect(left = 0f, top = fillEdge, right = viewport, bottom = viewport) {
                scale(1f, 1f - .67f * wave(barProgress), pivot) {
                    drawPart(index, cutout)
                }
            }
        }
        drawPart(5, lerp(tint, cutout, phase(progress, .25f, .70f)))
    }

    private fun DrawScope.drawMenu(progress: Float, tint: Color) {
        val topToBottom = menuLines
        for ((position, index) in topToBottom.withIndex()) {
            val shift = wave(phase(progress, .06f * position, .83f + .06f * position))
            val direction = (position - 1).toFloat()
            translate(left = direction * 2.1f * shift, top = -direction * 1.35f * shift) {
                rotate(direction * 11f * shift, parts[index].center) {
                    val width = 1f - (if (position == 1) .63f else .28f) * shift
                    scale(width, 1f, parts[index].center) { drawPart(index, tint) }
                }
            }
        }
    }

    private val storeBody = if (iconRes == R.drawable.ic_store) compound(1, 2, 3) else null
    private val statsFrame = if (iconRes == R.drawable.ic_agent_statistics) compound(3, 4) else Path()
    private val menuLines = if (iconRes == R.drawable.ic_menu || iconRes == R.drawable.ic_agent_menu) {
        parts.indices.sortedBy { parts[it].center.y }
    } else emptyList()

    private fun compound(vararg indices: Int) = Path().apply {
        indices.forEach { addPath(parts[it].path) }
    }

    private fun DrawScope.growFill(index: Int, progress: Float, color: Color) {
        if (progress > 0f) {
            scale(progress, progress, parts[index].center) { drawPart(index, color) }
        }
    }

    private fun DrawScope.drawPart(index: Int, color: Color) {
        val part = parts[index]
        if (part.stroke == null) drawPath(part.path, color)
        else drawPath(part.path, color, style = part.stroke)
    }

    private fun phase(progress: Float, start: Float, end: Float) =
        FastOutSlowInEasing.transform(((progress - start) / (end - start)).coerceIn(0f, 1f))

    private fun wave(progress: Float) = if (progress > 0f && progress < 1f) {
        sin(PI * progress).toFloat()
    } else 0f

    private data class Part(val path: Path, val center: Offset, val stroke: Stroke?)
}
