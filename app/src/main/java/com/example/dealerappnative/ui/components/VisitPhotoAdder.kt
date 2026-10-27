package com.example.dealerappnative.ui.components

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.material3.Text
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.VisitCameraPhotos
import com.example.dealerappnative.model.AgentVisitSession
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Active visits accept new photos only through the camera. Existing photos stay readable. */
@Composable
internal fun VisitPhotoAdder(session: AgentVisitSession, onAddPhotos: (List<String>) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
    val currentSession by rememberUpdatedState(session)
    val currentAdd by rememberUpdatedState(onAddPhotos)
    // ActivityResult + URI survive activity/process recreation while the external camera is open.
    var pendingCamera by rememberSaveable(session.startedAt) { mutableStateOf<String?>(null) }
    var preparing by remember { mutableStateOf(false) }
    val camera = rememberLauncherForActivityResult(VisitTakePictureContract()) { success ->
        val pending = pendingCamera?.let(Uri::parse)
        pendingCamera = null
        if (pending != null) scope.launch {
            val owner = currentSession.startedAt
            val readable = withContext(Dispatchers.IO) {
                VisitCameraPhotos.complete(context, pending, success && currentSession.isActive && currentSession.photoUris.size < 6)
            }
            if (readable != null) {
                if (currentSession.isActive && currentSession.startedAt == owner && currentSession.photoUris.size < 6)
                    currentAdd(listOf(readable))
                else withContext(Dispatchers.IO) { VisitCameraPhotos.discard(context, pending) }
            } else if (success) Toast.makeText(context, "Не удалось добавить фото. Попробуйте ещё раз.", Toast.LENGTH_LONG).show()
        }
    }
    val takePhoto: () -> Unit = {
        if (!preparing && pendingCamera == null && currentSession.isActive && currentSession.photoUris.size < 6) {
            keyboard?.hide()
            preparing = true
            scope.launch {
                var pending: Uri? = null
                try {
                    val uri = withContext(Dispatchers.IO) { VisitCameraPhotos.create(context) }
                    pending = uri
                    if (currentSession.isActive && currentSession.photoUris.size < 6) {
                        pendingCamera = uri.toString()
                        camera.launch(uri)
                    } else withContext(Dispatchers.IO) { VisitCameraPhotos.discard(context, uri) }
                } catch (error: Exception) {
                    pendingCamera = null
                    pending?.let { withContext(Dispatchers.IO) { VisitCameraPhotos.discard(context, it) } }
                    Toast.makeText(context, "Не удалось открыть камеру", Toast.LENGTH_LONG).show()
                } finally { preparing = false }
            }
        }
    }
    Column(Modifier.size(96.dp).softCardPress(enabled = !preparing && pendingCamera == null, onClick = takePhoto)
        .clip(RoundedCornerShape(12.dp)).border(1.dp, DealerDesign.divider, RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        DealerIcon(R.drawable.ic_visit_camera, DealerDesign.accent, 24.dp)
        Spacer(Modifier.height(6.dp))
        Text("Добавить фото", style = AgentVisitDesign.detail, color = DealerDesign.accent)
    }
}

/** Explicit, temporary per-file grants also work with camera apps on older Android versions. */
internal class VisitTakePictureContract : ActivityResultContracts.TakePicture() {
    override fun createIntent(context: Context, input: Uri): Intent = super.createIntent(context, input).apply {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        clipData = ClipData.newRawUri("Visit photo", input)
    }
}
