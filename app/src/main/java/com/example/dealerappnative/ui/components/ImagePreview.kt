package com.example.dealerappnative.ui.components

import android.content.ContentResolver
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import com.example.dealerappnative.R
import com.example.dealerappnative.data.loadLocalPhoto
import com.example.dealerappnative.ui.theme.DealerDesign
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs

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
    var hidden by mutableStateOf(false)
    var thumbnail: ImageBitmap? = null
    var coordinates: LayoutCoordinates? = null

    // The thumbnail and the dialog are in separate Compose windows.
    fun screenBounds(): ThumbnailBounds? {
        val layout = coordinates?.takeIf { it.isAttached } ?: return null
        val origin = layout.positionOnScreen()
        val visible = layout.boundsInWindow()
        if (!origin.isSpecified || visible.isEmpty) return null
        val size = Size(layout.size.width.toFloat(), layout.size.height.toFloat())
        return ThumbnailBounds(Rect(origin, size), visible.translate(origin - layout.positionInWindow()))
    }
}

internal fun photoPreviewTrigger(anchor: PhotoPreviewAnchor, label: String, onOpen: () -> Unit): Modifier =
    Modifier.onGloballyPositioned { anchor.coordinates = it }
        .graphicsLayer { alpha = if (anchor.hidden) 0f else 1f }
        .softCardPress(onClick = onOpen)
        .semantics { contentDescription = "Открыть фото: $label" }

internal data class ThumbnailBounds(val full: Rect, val visible: Rect) {
    fun translate(offset: Offset) = ThumbnailBounds(full.translate(offset), visible.translate(offset))
}

private val photoTravelEasing = CubicBezierEasing(.37f, .05f, .42f, 1f)

private data class LoadedPhoto(val bitmap: ImageBitmap?, val blend: Animatable<Float, AnimationVector1D>)
private data class PhotoTransition(
    val page: Int,
    val bounds: ThumbnailBounds?,
    val source: PhotoPreviewAnchor?,
    val zoom: PhotoZoom,
    val pageOffset: Float,
    val drag: Float,
    val clipPath: Path = Path()
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
    val opening = remember { Animatable(0f) }
    val exit = remember { Animatable(0f) }
    val loaded = remember { mutableStateMapOf<Uri, LoadedPhoto>() }
    val zooms = remember { mutableStateMapOf<Uri, PhotoZoom>() }
    val scope = rememberCoroutineScope()
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    var origin by remember { mutableStateOf(Offset.Unspecified) }
    var viewport by remember { mutableStateOf(Rect.Zero) }
    var drag by remember { mutableFloatStateOf(0f) }
    var rebound by remember { mutableStateOf<Job?>(null) }
    var opened by remember { mutableStateOf(false) }
    var closing by remember { mutableStateOf(false) }
    var transition by remember { mutableStateOf<PhotoTransition?>(null) }
    val ready = origin.isSpecified && !viewport.isEmpty
    val interactive = opened && !closing
    val pageSwipeEnabled by remember(photos.size) {
        derivedStateOf { opened && !closing && photos.size > 1 && drag == 0f }
    }
    val spacing = 16.dp
    val density = LocalDensity.current
    val pageSpacing = with(density) { spacing.toPx() }
    var releaseMotion by remember { mutableStateOf<PhotoDismissMotion?>(null) }

    fun exitProgress(): Float = releaseMotion?.progress(exit.value) ?: exit.value

    fun capture(): PhotoTransition {
        val page = pager.currentPage.coerceIn(photos.indices)
        val uri = photos[page]
        val anchor = anchorFor(uri)
        val pageOffset = pager.getOffsetDistanceInPages(page) * (viewport.width + pageSpacing) * if (rtl) -1 else 1
        return PhotoTransition(page, anchor?.screenBounds()?.translate(-origin), anchor,
            zooms[uri] ?: PhotoZoom(), pageOffset, drag)
    }

    fun dismiss(releaseVelocity: Float? = null) {
        if (closing) return
        rebound?.cancel()
        if (opened) {
            transition = capture()
            releaseMotion = releaseVelocity?.let { PhotoDismissMotion(it, density.density) }
        }
        closing = true
    }

    val activeUri = photos[pager.settledPage.coerceIn(photos.indices)]
    val sharedAnchor = if (interactive) anchorFor(activeUri) else transition?.source
    DisposableEffect(sharedAnchor) {
        if (sharedAnchor?.thumbnail != null) sharedAnchor.hidden = true
        onDispose { sharedAnchor?.hidden = false }
    }

    LaunchedEffect(ready, closing) {
        if (closing) {
            if (opened) {
                val motion = releaseMotion
                val spec = if (motion == null) tween<Float>(280, easing = photoTravelEasing)
                    else tween(motion.durationMillis, easing = LinearEasing)
                exit.animateTo(1f, spec)
            } else {
                opening.animateTo(0f, tween(190, easing = photoTravelEasing))
            }
            transition?.source?.hidden = false
            if (transition?.bounds != null) withFrameNanos {}
            onDismiss()
        } else if (ready && !opened) {
            transition = capture()
            opening.animateTo(1f, tween(290, easing = photoTravelEasing))
            withFrameNanos { opened = true }
        }
    }

    Dialog(
        onDismissRequest = { dismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            dismissOnClickOutside = false
        )
    ) {
        PreviewSystemBars()
        Box(Modifier.fillMaxSize().onGloballyPositioned { origin = it.positionOnScreen() }) {
            Canvas(Modifier.matchParentSize()) {
                val opacity = opening.value * (1f - exitProgress()) * (1f - .55f * dragFraction(drag, viewport))
                drawRect(Color(0xFF18151D), alpha = .97f * opacity)
            }
            Box(Modifier.fillMaxSize().safeDrawingPadding()) {
                HorizontalPager(
                    state = pager,
                    key = { photos[it] },
                    userScrollEnabled = pageSwipeEnabled,
                    pageSpacing = spacing,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 62.dp)
                        .onGloballyPositioned { viewport = it.boundsInRoot() }
                        .graphicsLayer { alpha = if (interactive) 1f else 0f }
                ) { page ->
                    val uri = photos[page]
                    val context = LocalContext.current
                    LaunchedEffect(uri, context) {
                        val bitmap = loadLocalPhoto(context, uri, 1600)?.asImageBitmap()
                        val blend = Animatable(0f)
                        loaded[uri] = LoadedPhoto(bitmap, blend)
                        if (bitmap != null) blend.animateTo(1f, tween(170))
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
                                zoom = { zooms[uri] ?: PhotoZoom() }, onZoom = { zooms[uri] = it },
                                onDrag = {
                                    rebound?.cancel()
                                    drag = it
                                },
                                onDragEnd = { close, velocity ->
                                    if (close) dismiss(velocity)
                                    else rebound = scope.launch {
                                        animate(drag, 0f, animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioNoBouncy,
                                            stiffness = Spring.StiffnessMediumLow,
                                            visibilityThreshold = .5f
                                        )) { value, _ ->
                                            drag = value
                                        }
                                    }
                                }
                            ) { frame ->
                                val movement = if (pager.settledPage == page) drag else 0f
                                drawPhoto(thumb, result, frame, frame, zooms[uri] ?: PhotoZoom(), movement)
                            }
                            interactive && result != null ->
                                Text("Фото недоступно", style = DealerDesign.detail, color = Color.White)
                            interactive -> CircularProgressIndicator(Modifier.size(28.dp), color = Color.White)
                        }
                    }
                }
                SoftIconButton(
                    { dismiss() },
                    Modifier.align(Alignment.TopEnd).padding(12.dp).size(48.dp)
                        .graphicsLayer { alpha = opening.value * (1f - exitProgress()) * (1f - dragFraction(drag, viewport)) }
                        .background(Color.White.copy(alpha = .12f), CircleShape)
                        .semantics { contentDescription = "Закрыть фото" }
                ) { DealerIcon(R.drawable.ic_agent_clear, Color.White, 22.dp) }
                if (photos.size > 1) {
                    Text(
                        "${pager.currentPage + 1} / ${photos.size}",
                        style = DealerDesign.detail,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp)
                            .graphicsLayer { alpha = opening.value * (1f - exitProgress()) * (1f - dragFraction(drag, viewport)) }
                            .background(Color.White.copy(alpha = .12f), CircleShape)
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }

            val motion = transition
            if (!interactive && motion != null) {
                val uri = photos[motion.page]
                val thumb = motion.source?.thumbnail
                val photo = loaded[uri]
                if (thumb != null || photo?.bitmap != null) {
                    Canvas(Modifier.matchParentSize()) {
                        val fraction = if (opened) 1f - exitProgress() else opening.value
                        val pageFrame = viewport.translate(Offset(motion.pageOffset, 0f))
                        val momentum = releaseMotion?.verticalOffset(exit.value) ?: 0f
                        drawPhoto(thumb, photo, viewport, pageFrame, motion.zoom, motion.drag, fraction, motion, momentum)
                    }
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
        window.setWindowAnimations(0)
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
}

private fun dragFraction(drag: Float, frame: Rect): Float =
    (abs(drag) / (frame.height * .45f).coerceAtLeast(1f)).coerceIn(0f, 1f)

private fun DrawScope.drawPhoto(
    thumbnail: ImageBitmap?,
    photo: LoadedPhoto?,
    viewport: Rect,
    pageFrame: Rect,
    zoom: PhotoZoom,
    drag: Float,
    fraction: Float = 1f,
    motion: PhotoTransition? = null,
    momentum: Float = 0f
) {
    val bounds = motion?.bounds
    val dragScale = 1f - .16f * dragFraction(drag, viewport)
    val fitFrame = pageFrame.translate(Offset(0f, drag))
    val scale = zoom.scale * dragScale
    val blend = (photo?.blend?.value ?: 0f) * if (bounds == null) 1f
        else ((fraction - .35f) / .55f).coerceIn(0f, 1f)
    val visibility = if (bounds == null) fraction else 1f

    fun drawLayer(bitmap: ImageBitmap, alpha: Float) {
        if (alpha <= 0f) return
        val fit = photoRect(bitmap, fitFrame)
        val drawn = Size(fit.width * scale, fit.height * scale)
        val target = Rect(fit.center + zoom.offset - Offset(drawn.width / 2f, drawn.height / 2f), drawn)
        val source = if (bounds != null && motion.source != null) {
            val inset = motion.source.inset.toPx().coerceAtMost(minOf(bounds.full.width, bounds.full.height) * .45f)
            photoRect(bitmap, bounds.full.deflate(inset), motion.source.crop)
        } else target
        val rect = lerp(source, target, fraction).translate(Offset(0f, momentum))
        withTransform({
            translate(rect.left, rect.top)
            scale(rect.width / bitmap.width, rect.height / bitmap.height, Offset.Zero)
        }) {
            drawImage(bitmap, dstSize = IntSize(bitmap.width, bitmap.height), alpha = alpha * visibility,
                filterQuality = FilterQuality.Medium)
        }
    }

    val full = photo?.bitmap
    val matchingAspect = thumbnail != null && full != null &&
        abs(thumbnail.width.toFloat() / thumbnail.height - full.width.toFloat() / full.height) < .005f
    val layers: DrawScope.() -> Unit = {
        if (thumbnail != null) {
            if (blend < 1f) drawLayer(thumbnail, if (full == null || matchingAspect) 1f else 1f - blend)
            if (full != null) drawLayer(full, blend)
        } else if (full != null) drawLayer(full, if (bounds == null) blend else 1f)
    }

    if (bounds == null || fraction >= 1f) {
        clipRect(viewport.left, viewport.top, viewport.right, viewport.bottom, block = layers)
    } else {
        val clip = lerp(bounds.visible, viewport, fraction)
        val radius = (motion.source?.radius ?: 12.dp).toPx() * (1f - fraction)
        val mask = checkNotNull(motion).clipPath.apply {
            rewind()
            addRoundRect(RoundRect(clip, CornerRadius(radius)))
        }
        clipPath(mask, block = layers)
    }
}

internal fun photoRect(bitmap: ImageBitmap, frame: Rect, crop: Boolean = false): Rect {
    val width = frame.width / bitmap.width
    val height = frame.height / bitmap.height
    val scale = if (crop) maxOf(width, height) else minOf(width, height)
    val size = Size(bitmap.width * scale, bitmap.height * scale)
    return Rect(frame.center - Offset(size.width / 2f, size.height / 2f), size)
}
