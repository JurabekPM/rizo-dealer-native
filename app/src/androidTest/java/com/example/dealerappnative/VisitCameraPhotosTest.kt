package com.example.dealerappnative

import android.content.Intent
import android.app.Activity
import android.os.Build
import android.util.Size
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.dealerappnative.data.VisitCameraPhotos
import com.example.dealerappnative.ui.components.VisitTakePictureContract
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/** Does not open the camera or user gallery; only freshly generated test JPEGs are touched. */
@RunWith(AndroidJUnit4::class)
class VisitCameraPhotosTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun captureUriSurvivesRoundTripAndStoresAReadableFullImage() {
        val uri = VisitCameraPhotos.create(context)
        try {
            val bitmap = Bitmap.createBitmap(32, 24, Bitmap.Config.ARGB_8888)
            try { context.contentResolver.openOutputStream(uri)!!.use {
                assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it))
            } } finally { bitmap.recycle() }
            val restored = Uri.parse(uri.toString())
            assertEquals(uri.toString(), VisitCameraPhotos.complete(context, restored, true))
            if (Build.VERSION.SDK_INT >= 29) {
                val thumbnail = context.contentResolver.loadThumbnail(restored, Size(256, 256), null)
                assertTrue(thumbnail.width > 0 && thumbnail.height > 0)
                thumbnail.recycle()
            }
            context.contentResolver.openInputStream(restored)!!.use {
                val image = BitmapFactory.decodeStream(it)!!
                assertEquals(32, image.width); assertEquals(24, image.height); image.recycle()
            }
        } finally { VisitCameraPhotos.discard(context, uri) }
    }

    @Test fun cancelledAndEmptyCapturesDoNotBecomeVisitEvidence() {
        val cancelled = VisitCameraPhotos.create(context)
        val empty = VisitCameraPhotos.create(context)
        try {
            assertNull(VisitCameraPhotos.complete(context, cancelled, false))
            assertNull(VisitCameraPhotos.complete(context, empty, true))
            assertTrue(runCatching { context.contentResolver.openInputStream(cancelled)?.close() }.isFailure)
            assertTrue(runCatching { context.contentResolver.openInputStream(empty)?.close() }.isFailure)
            assertNull(VisitCameraPhotos.complete(context, Uri.parse("content://unrelated/photos/1"), true))
        } finally {
            VisitCameraPhotos.discard(context, cancelled); VisitCameraPhotos.discard(context, empty)
        }
    }

    @Test fun cameraGetsOnlyATemporaryGrantAndProviderIsNotExported() {
        val uri = VisitCameraPhotos.create(context)
        try {
            val intent = VisitTakePictureContract().createIntent(context, uri)
            assertEquals(MediaStore.ACTION_IMAGE_CAPTURE, intent.action)
            assertFalse(VisitTakePictureContract().parseResult(Activity.RESULT_CANCELED, null))
            assertTrue(VisitTakePictureContract().parseResult(Activity.RESULT_OK, null))
            assertEquals(uri, intent.clipData!!.getItemAt(0).uri)
            assertTrue(intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION != 0)
            assertTrue(intent.flags and Intent.FLAG_GRANT_WRITE_URI_PERMISSION != 0)
            val provider = context.packageManager.resolveContentProvider(uri.authority!!, 0)!!
            assertFalse(provider.exported); assertTrue(provider.grantUriPermissions)
        } finally { VisitCameraPhotos.discard(context, uri) }
    }
}
