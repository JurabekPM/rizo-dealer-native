package com.example.dealerappnative.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal suspend fun loadLocalPhoto(context: Context, uri: Uri, maxEdge: Int): Bitmap? = withContext(Dispatchers.IO) {
    val asset = if (uri.scheme == "asset") uri.path?.removePrefix("/") else null
    if (Build.VERSION.SDK_INT >= 28) {
        val oriented = runCatching {
            val source = if (asset != null) ImageDecoder.createSource(context.assets, asset)
                else ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                val width = info.size.width
                val height = info.size.height
                val scale = maxEdge.toDouble() / maxOf(width, height)
                if (scale < 1.0) decoder.setTargetSize(
                    (width * scale).toInt().coerceAtLeast(1),
                    (height * scale).toInt().coerceAtLeast(1)
                )
            }
        }.getOrNull()
        if (oriented != null) return@withContext oriented
    }

    fun open() = if (asset != null) context.assets.open(asset) else context.contentResolver.openInputStream(uri)
    runCatching {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        open()?.use { BitmapFactory.decodeStream(it, null, options) }
        var sample = 1
        while (maxOf(options.outWidth, options.outHeight) / sample > maxEdge) sample *= 2
        options.inJustDecodeBounds = false
        options.inSampleSize = sample
        open()?.use { BitmapFactory.decodeStream(it, null, options) }
    }.getOrNull()
}
