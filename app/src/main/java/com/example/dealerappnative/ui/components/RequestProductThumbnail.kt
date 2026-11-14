package com.example.dealerappnative.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    selected: Boolean = false
) {
    val photo = product.photo
    val imageSize = size * (2f / 3f)
    val pixels = with(LocalDensity.current) { imageSize.roundToPx() }.coerceAtLeast(1)
    val image = rememberProductImage(photo, pixels)
    val shape = RoundedCornerShape(12.dp)
    PreviewableImage(imageResourceUri(photo).takeIf { image.bitmap != null }, product.name) { preview ->
        Box(Modifier.size(size).then(preview).clip(shape).background(RequestPickerDesign.thumbnail)
            .then(if (selected) Modifier.border(1.dp, DealerDesign.accent.copy(alpha = .6f), shape) else Modifier),
            contentAlignment = Alignment.Center) {
            if (image.bitmap != null) Image(image.bitmap, null, contentScale = ContentScale.Fit, modifier = Modifier.size(imageSize))
            else if (photo == null || image.loaded) DealerIcon(R.drawable.ic_box)
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
