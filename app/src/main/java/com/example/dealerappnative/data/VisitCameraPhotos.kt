package com.example.dealerappnative.data

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/** Private, durable photos. Only the chosen capture URI is granted to the camera app.
 * https://developer.android.com/training/secure-file-sharing/setup-sharing
 */
internal object VisitCameraPhotos {
    fun create(context: Context): Uri {
        val directory = File(context.filesDir, "visit_photos/camera")
        check(directory.isDirectory || directory.mkdirs()) { "Cannot create visit photo directory" }
        val file = File.createTempFile("visit_", ".jpg", directory)
        return try { FileProvider.getUriForFile(context, authority(context), file) }
        catch (error: Exception) { file.delete(); throw error }
    }

    /** Cancel, failed capture, inactive visit or a full photo list must not add evidence. */
    fun complete(context: Context, uri: Uri, accepted: Boolean): String? {
        if (!owns(context, uri)) return null
        val valid = accepted && runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            bounds.outWidth > 0 && bounds.outHeight > 0
        }.getOrDefault(false)
        if (valid) return uri.toString()
        discard(context, uri)
        return null
    }

    fun discard(context: Context, uri: Uri) {
        if (owns(context, uri)) runCatching { context.contentResolver.delete(uri, null, null) }
    }

    private fun authority(context: Context) = "${context.packageName}.visit_photos"
    private fun owns(context: Context, uri: Uri): Boolean = uri.scheme == "content" &&
        uri.authority == authority(context) && uri.pathSegments.let {
            it.size == 2 && it[0] == "camera" && it[1].startsWith("visit_") && it[1].endsWith(".jpg")
        }
}
