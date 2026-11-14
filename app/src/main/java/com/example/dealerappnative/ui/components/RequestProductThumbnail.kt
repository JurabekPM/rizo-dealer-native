package com.example.dealerappnative.ui.components

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.dealerappnative.R
import com.example.dealerappnative.model.PickerProduct
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.RequestPickerDesign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** 48 dp tile with a sampled 32 dp image. Load a larger copy only when previewing. */
@Composable
internal fun RequestProductThumbnail(
    product: PickerProduct,
    size: Dp = 48.dp,
    selected: Boolean = false,
    previewEnabled: Boolean = false
) {
    val photo = product.photo
    val imageSize = size * (2f / 3f)
    val pixels = with(LocalDensity.current) { imageSize.roundToPx() }.coerceAtLeast(1)
    val image = rememberProductImage(photo, pixels)
    val shape = RoundedCornerShape(12.dp)
    var preview by rememberSaveable(product.id) { mutableStateOf(false) }
    val openPreview = if (previewEnabled && photo != null) Modifier.softCardPress { preview = true }
        .semantics { contentDescription = "Открыть фото: ${product.name}" } else Modifier
    Box(Modifier.size(size).then(openPreview).clip(shape).background(RequestPickerDesign.thumbnail)
        .then(if (selected) Modifier.border(1.dp, DealerDesign.accent.copy(alpha = .6f), shape) else Modifier),
        contentAlignment = Alignment.Center) {
        if (image.bitmap != null) Image(image.bitmap, null, contentScale = ContentScale.Fit, modifier = Modifier.size(imageSize))
        else if (photo == null || image.loaded) DealerIcon(R.drawable.ic_box)
    }
    if (preview && previewEnabled && photo != null) {
        ProductImagePreview(photo, product.name, onDismiss = { preview = false })
    }
}

@Composable
private fun ProductImagePreview(photo: Int, name: String, onDismiss: () -> Unit) {
    val visibility = remember { MutableTransitionState(false).apply { targetState = true } }
    val dismiss = { visibility.targetState = false }
    val image = rememberProductImage(photo, 1200)
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
                        image.bitmap != null -> Image(image.bitmap, name,
                            Modifier.fillMaxSize().padding(24.dp), contentScale = ContentScale.Fit)
                        image.loaded -> Text("Фото недоступно", style = DealerDesign.detail, color = DealerDesign.secondary)
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

private data class ProductImage(val bitmap: ImageBitmap? = null, val loaded: Boolean = false)

@Composable
private fun rememberProductImage(photo: Int?, maxPixels: Int): ProductImage {
    val resources = LocalContext.current.resources
    val image by produceState(ProductImage(), photo, maxPixels, resources) {
        value = ProductImage()
        val bitmap = photo?.let { id ->
            withContext(Dispatchers.IO) {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true; inScaled = false }
                BitmapFactory.decodeResource(resources, id, options)
                val largestEdge = maxOf(options.outWidth, options.outHeight)
                var sample = 1
                while (largestEdge / (sample * 2) >= maxPixels) sample *= 2
                options.inJustDecodeBounds = false
                options.inSampleSize = sample
                BitmapFactory.decodeResource(resources, id, options)?.asImageBitmap()
            }
        }
        value = ProductImage(bitmap, loaded = true)
    }
    return image
}
