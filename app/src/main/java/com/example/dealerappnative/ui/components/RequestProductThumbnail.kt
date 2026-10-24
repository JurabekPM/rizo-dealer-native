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
import com.example.dealerappnative.data.RequestCatalogProduct
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.RequestPickerDesign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Original Figma 48px tile / 32px image; decode sampled photos off the UI thread. */
@Composable
internal fun RequestProductThumbnail(product: RequestCatalogProduct, size: Dp = 48.dp, selected: Boolean = false) {
    val resources = LocalContext.current.resources
    val imageSize = size * (2f / 3f)
    val pixels = with(LocalDensity.current) { imageSize.roundToPx() }.coerceAtLeast(1)
    val bitmap by produceState<ImageBitmap?>(null, product.photo, pixels) {
        value = product.photo?.let { id -> withContext(Dispatchers.IO) {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true; inScaled = false }
            BitmapFactory.decodeResource(resources, id, options)
            var sample = 1
            while (minOf(options.outWidth, options.outHeight) / (sample * 2) >= pixels) sample *= 2
            options.inJustDecodeBounds = false
            options.inSampleSize = sample
            BitmapFactory.decodeResource(resources, id, options)?.asImageBitmap()
        } }
    }
    Box(Modifier.size(size).clip(RoundedCornerShape(12.dp)).background(RequestPickerDesign.thumbnail)
        .then(if (selected) Modifier.border(1.dp, DealerDesign.accent.copy(alpha = .6f), RoundedCornerShape(12.dp)) else Modifier),
        contentAlignment = Alignment.Center) {
        bitmap?.let { Image(it, null, contentScale = ContentScale.Fit, modifier = Modifier.size(imageSize)) }
            ?: if (product.photo == null) DealerIcon(R.drawable.ic_box) else Unit
    }
}
