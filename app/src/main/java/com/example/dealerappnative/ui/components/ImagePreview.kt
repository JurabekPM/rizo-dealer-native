package com.example.dealerappnative.ui.components

import android.content.ContentResolver
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import com.example.dealerappnative.R
import com.example.dealerappnative.data.loadLocalPhoto
import com.example.dealerappnative.ui.theme.DealerDesign
import kotlin.math.roundToInt

@Composable
internal fun imageResourceUri(@DrawableRes photo: Int?): Uri? {
    val packageName = LocalContext.current.packageName
    return remember(packageName, photo) {
        photo?.let { Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(packageName).appendPath(it.toString()).build() }
    }
}

@Composable
internal fun PreviewableImage(
    uri: Uri?,
    label: String,
    thumbnail: ImageBitmap?,
    crop: Boolean = false,
    inset: Dp = 0.dp,
    radius: Dp = 12.dp,
    content: @Composable (Modifier) -> Unit
) {
    var open by rememberSaveable(uri) { mutableStateOf(false) }
    val anchor = remember(uri, crop, inset, radius) { PhotoPreviewAnchor(crop, inset, radius) }
    SideEffect { anchor.thumbnail = thumbnail }
    content(if (uri == null) Modifier else photoPreviewTrigger(anchor, label) { open = true })
    if (open && uri != null) {
        ImagePreviewGallery(listOf(uri), startPage = 0, label = label, anchorFor = { anchor }, onDismiss = { open = false })
    }
}

internal class PhotoPreviewAnchor(val crop: Boolean, val inset: Dp = 0.dp, val radius: Dp = 12.dp) {
    var thumbnail: ImageBitmap? = null
    var coordinates: LayoutCoordinates? = null

    // The thumbnail and the dialog are in separate Compose windows.
    fun screenBounds(): Rect? {
        val layout = coordinates?.takeIf { it.isAttached } ?: return null
        val origin = layout.positionOnScreen()
        val visible = layout.boundsInWindow()
        if (!origin.isSpecified || visible.isEmpty) return null
        return visible.translate(origin - layout.positionInWindow())
    }
}

internal fun photoPreviewTrigger(anchor: PhotoPreviewAnchor, label: String, onOpen: () -> Unit): Modifier =
    Modifier.onGloballyPositioned { anchor.coordinates = it }.softCardPress(onClick = onOpen)
        .semantics { contentDescription = "Открыть фото: $label" }

private data class PhotoZoom(val scale: Float = 1f, val offset: Offset = Offset.Zero)
private data class LoadedPhoto(val bitmap: ImageBitmap?)
private data class PhotoTransition(
    val page: Int,
    val bounds: Rect?,
    val source: PhotoPreviewAnchor?,
    val zoom: PhotoZoom
)

@Composable
internal fun ImagePreviewGallery(
    photos: List<Uri>,
    startPage: Int,
    label: String,
    anchorFor: (Uri) -> PhotoPreviewAnchor?,
    onDismiss: () -> Unit
) {
    val pager = rememberPagerState(initialPage = startPage) { photos.size }
    val progress = remember { Animatable(0f) }
    val loaded = remember { mutableStateMapOf<Uri, LoadedPhoto>() }
    val zooms = remember { mutableStateMapOf<Uri, PhotoZoom>() }
    var origin by remember { mutableStateOf(Offset.Unspecified) }
    var viewport by remember { mutableStateOf(Rect.Zero) }
    var interactive by remember { mutableStateOf(false) }
    var closing by remember { mutableStateOf(false) }
    var transition by remember { mutableStateOf<PhotoTransition?>(null) }
    val ready = origin.isSpecified && !viewport.isEmpty

    LaunchedEffect(ready, closing) {
        if (!ready) {
            if (closing) onDismiss()
            return@LaunchedEffect
        }
        val page = pager.currentPage
        val uri = photos[page]
        val anchor = anchorFor(uri)
        transition = PhotoTransition(page, anchor?.screenBounds()?.translate(-origin), anchor, zooms[uri] ?: PhotoZoom())
        if (closing) {
            interactive = false
            progress.animateTo(0f, tween(260, easing = FastOutSlowInEasing))
            onDismiss()
        } else {
            progress.animateTo(1f, tween(330, easing = FastOutSlowInEasing))
            interactive = true
        }
    }

    Dialog(
        onDismissRequest = { closing = true },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            dismissOnClickOutside = false
        )
    ) {
        PreviewSystemBars()
        Box(Modifier.fillMaxSize().onGloballyPositioned { origin = it.positionOnScreen() }) {
            Canvas(Modifier.matchParentSize()) { drawRect(Color(0xFF18151D), alpha = .97f * progress.value) }
            Box(Modifier.fillMaxSize().safeDrawingPadding()) {
                HorizontalPager(
                    state = pager,
                    key = { photos[it] },
                    userScrollEnabled = interactive && photos.size > 1,
                    pageSpacing = 16.dp,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 62.dp)
                        .onGloballyPositioned { viewport = it.boundsInRoot() }
                        .graphicsLayer { alpha = if (interactive) 1f else 0f }
                ) { page ->
                    val uri = photos[page]
                    val context = LocalContext.current
                    LaunchedEffect(uri, context) {
                        loaded[uri] = LoadedPhoto(loadLocalPhoto(context, uri, 1600)?.asImageBitmap())
                    }
                    DisposableEffect(uri) {
                        onDispose {
                            loaded.remove(uri)
                            zooms.remove(uri)
                        }
                    }
                    val result = loaded[uri]
                    val thumb = anchorFor(uri)?.thumbnail
                    val bitmap = result?.bitmap ?: thumb
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        when {
                            bitmap != null -> ZoomablePhoto(
                                bitmap, label, interactive && pager.settledPage == page,
                                zoom = { zooms[uri] ?: PhotoZoom() }, onZoom = { zooms[uri] = it }
                            )
                            interactive && result != null ->
                                Text("Фото недоступно", style = DealerDesign.detail, color = Color.White)
                            interactive -> CircularProgressIndicator(Modifier.size(28.dp), color = Color.White)
                        }
                    }
                }
                SoftIconButton(
                    { closing = true },
                    Modifier.align(Alignment.TopEnd).padding(12.dp).size(48.dp)
                        .graphicsLayer { alpha = progress.value }
                        .background(Color.White.copy(alpha = .12f), CircleShape)
                        .semantics { contentDescription = "Закрыть фото" }
                ) { DealerIcon(R.drawable.ic_agent_clear, Color.White, 22.dp) }
                if (photos.size > 1) {
                    Text(
                        "${pager.currentPage + 1} / ${photos.size}",
                        style = DealerDesign.detail,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp)
                            .graphicsLayer { alpha = progress.value }
                            .background(Color.White.copy(alpha = .12f), CircleShape)
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }

            val motion = transition
            if (!interactive && motion != null) {
                val uri = photos[motion.page]
                val anchor = anchorFor(uri)
                val bitmap = if (closing) loaded[uri]?.bitmap ?: anchor?.thumbnail
                    else anchor?.thumbnail ?: loaded[uri]?.bitmap
                if (bitmap != null) {
                    PhotoBoundsAnimation(bitmap, motion, viewport, progress, Modifier.matchParentSize())
                }
            }
        }
    }
}

@Composable
private fun PreviewSystemBars() {
    val view = LocalView.current
    val window = (view.parent as? DialogWindowProvider)?.window ?: return
    SideEffect {
        window.setDimAmount(0f)
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
}

@Composable
private fun ZoomablePhoto(
    bitmap: ImageBitmap,
    label: String,
    enabled: Boolean,
    zoom: () -> PhotoZoom,
    onZoom: (PhotoZoom) -> Unit
) {
    val currentZoom by rememberUpdatedState(zoom)
    val updateZoom by rememberUpdatedState(onZoom)
    LaunchedEffect(enabled) {
        if (!enabled) updateZoom(PhotoZoom())
    }
    Box(
        Modifier.fillMaxSize().clipToBounds()
            .semantics { if (enabled) stateDescription = "${(zoom().scale * 100).roundToInt()}%" }
            .pointerInput(enabled, bitmap.width, bitmap.height) {
                if (!enabled) return@pointerInput
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    var pinching = false
                    do {
                        val event = awaitPointerEvent()
                        if (event.changes.any { it.isConsumed }) break
                        val previous = currentZoom()
                        pinching = pinching || event.changes.count { it.pressed } > 1
                        if (pinching || previous.scale > 1f) {
                            val scale = (previous.scale * event.calculateZoom()).coerceIn(1f, 4f)
                            val relativeZoom = scale / previous.scale
                            val frame = Rect(Offset.Zero, Size(size.width.toFloat(), size.height.toFloat()))
                            val fitted = photoRect(bitmap, frame)
                            val centroid = event.calculateCentroid(useCurrent = false)
                            val pivot = if (centroid.isSpecified) centroid - frame.center else Offset.Zero
                            val offset = pivot + (previous.offset - pivot) * relativeZoom + event.calculatePan()
                            val maxX = ((fitted.width * scale - frame.width) / 2f).coerceAtLeast(0f)
                            val maxY = ((fitted.height * scale - frame.height) / 2f).coerceAtLeast(0f)
                            updateZoom(PhotoZoom(scale, Offset(offset.x.coerceIn(-maxX, maxX), offset.y.coerceIn(-maxY, maxY))))
                            event.changes.forEach { if (it.position != it.previousPosition) it.consume() }
                        }
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        Image(bitmap, label, Modifier.fillMaxSize().graphicsLayer {
            val transform = zoom()
            scaleX = transform.scale
            scaleY = transform.scale
            translationX = transform.offset.x
            translationY = transform.offset.y
        }, contentScale = ContentScale.Fit)
    }
}

@Composable
private fun PhotoBoundsAnimation(
    bitmap: ImageBitmap,
    motion: PhotoTransition,
    viewport: Rect,
    progress: Animatable<Float, *>,
    modifier: Modifier
) {
    val thumbnailColor = DealerDesign.background
    Canvas(modifier) {
        val fraction = progress.value
        val anchor = motion.bounds
        val fitted = photoRect(bitmap, viewport)
        val target = Rect(fitted.center + motion.zoom.offset - Offset(fitted.width, fitted.height) * motion.zoom.scale / 2f,
            Size(fitted.width * motion.zoom.scale, fitted.height * motion.zoom.scale))
        if (anchor == null) {
            drawPreviewBitmap(bitmap, target, fraction)
        } else {
            val frame = lerp(anchor, viewport, fraction)
            val inset = (motion.source?.inset ?: 0.dp).toPx()
            val fullThumbnail = anchor.deflate(inset.coerceAtMost(minOf(anchor.width, anchor.height) * .45f))
            val image = lerp(photoRect(bitmap, fullThumbnail, motion.source?.crop == true), target, fraction)
            val radius = (motion.source?.radius ?: 12.dp).toPx() * (1f - fraction)
            val path = Path().apply { addRoundRect(RoundRect(frame, CornerRadius(radius))) }
            clipPath(path) {
                drawRect(thumbnailColor.copy(alpha = 1f - fraction), frame.topLeft, frame.size)
                drawPreviewBitmap(bitmap, image)
            }
        }
    }
}

private fun photoRect(bitmap: ImageBitmap, frame: Rect, crop: Boolean = false): Rect {
    val width = frame.width / bitmap.width
    val height = frame.height / bitmap.height
    val scale = if (crop) maxOf(width, height) else minOf(width, height)
    val size = Size(bitmap.width * scale, bitmap.height * scale)
    return Rect(frame.center - Offset(size.width / 2f, size.height / 2f), size)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPreviewBitmap(bitmap: ImageBitmap, rect: Rect, alpha: Float = 1f) {
    translate(rect.left, rect.top) {
        drawImage(bitmap, dstSize = IntSize(rect.width.roundToInt().coerceAtLeast(1), rect.height.roundToInt().coerceAtLeast(1)),
            alpha = alpha, filterQuality = FilterQuality.Medium)
    }
}
