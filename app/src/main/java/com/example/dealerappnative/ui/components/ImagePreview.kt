package com.example.dealerappnative.ui.components

import android.content.ContentResolver
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.dealerappnative.R
import com.example.dealerappnative.data.loadLocalPhoto
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun imageResourceUri(@DrawableRes photo: Int?): Uri? {
    val packageName = LocalContext.current.packageName
    return remember(packageName, photo) {
        photo?.let { Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(packageName).appendPath(it.toString()).build() }
    }
}

@Composable
internal fun PreviewableImage(uri: Uri?, label: String, content: @Composable (Modifier) -> Unit) {
    var open by rememberSaveable(uri) { mutableStateOf(false) }
    val trigger = if (uri != null) Modifier.softCardPress { open = true }
        .semantics { contentDescription = "Открыть фото: $label" } else Modifier
    content(trigger)
    if (open && uri != null) ImagePreviewDialog(uri, label, onDismiss = { open = false })
}

private data class ImagePreviewResult(val bitmap: ImageBitmap?)

@Composable
private fun ImagePreviewDialog(uri: Uri, name: String, onDismiss: () -> Unit) {
    val visibility = remember { MutableTransitionState(false).apply { targetState = true } }
    val dismiss = { visibility.targetState = false }
    val context = LocalContext.current
    val result by produceState<ImagePreviewResult?>(null, uri, context) {
        value = ImagePreviewResult(loadLocalPhoto(context, uri, 1200)?.asImageBitmap())
    }
    val bitmap = result?.bitmap
    LaunchedEffect(visibility.isIdle, visibility.currentState, visibility.targetState) {
        if (visibility.isIdle && !visibility.currentState && !visibility.targetState) onDismiss()
    }
    Dialog(onDismissRequest = dismiss) {
        BoxWithConstraints(Modifier.fillMaxWidth().heightIn(max = 440.dp)) {
            val previewHeight = minOf(maxWidth, maxHeight)
            AnimatedVisibility(
                visibleState = visibility,
                enter = fadeIn(tween(200)) + scaleIn(tween(240), initialScale = .94f),
                exit = fadeOut(tween(160)) + scaleOut(tween(180), targetScale = .96f)
            ) {
                val shape = RoundedCornerShape(24.dp)
                Box(Modifier.fillMaxWidth().height(previewHeight).clip(shape)
                    .background(DealerDesign.surface), contentAlignment = Alignment.Center) {
                    when {
                        bitmap != null -> Image(bitmap, name,
                            Modifier.fillMaxSize().padding(24.dp), contentScale = ContentScale.Fit)
                        result != null -> Text("Фото недоступно", style = DealerDesign.detail, color = DealerDesign.secondary)
                        else -> CircularProgressIndicator(Modifier.size(28.dp), color = DealerDesign.accent)
                    }
                    SoftIconButton(dismiss, Modifier.align(Alignment.TopEnd).padding(10.dp).size(44.dp)
                        .background(DealerDesign.background, RoundedCornerShape(14.dp))
                        .semantics { contentDescription = "Закрыть фото" }) {
                        DealerIcon(R.drawable.ic_agent_clear, DealerDesign.text, 20.dp)
                    }
                }
            }
        }
    }
}
