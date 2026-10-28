package com.example.dealerappnative.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Image documents stay on the device. No uploads, broad storage permission or visit-photo changes. */
internal suspend fun retainDealerProfileImage(context: Context, uri: Uri): Boolean = withContext(Dispatchers.IO) {
    runCatching {
        require(context.contentResolver.getType(uri)?.startsWith("image/") == true)
        context.contentResolver.openInputStream(uri)!!.use { stream ->
            val buffer = ByteArray(8192)
            var size = 0
            while (true) {
                val read = stream.read(buffer)
                if (read < 0) break
                size += read
                require(size <= 5 * 1024 * 1024)
            }
        }
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, bounds) }
        require(bounds.outWidth > 0 && bounds.outHeight > 0)
        context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        true
    }.getOrDefault(false)
}

internal suspend fun dealerProfileThumbnail(context: Context, value: String): Bitmap? = withContext(Dispatchers.IO) {
    if (value.isBlank()) return@withContext null
    runCatching {
        val uri = Uri.parse(value)
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, options) }
        var sample = 1
        while (maxOf(options.outWidth, options.outHeight) / sample > 512) sample *= 2
        options.inJustDecodeBounds = false
        options.inSampleSize = sample
        context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, options) }
    }.getOrNull()
}
