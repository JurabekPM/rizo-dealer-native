package com.example.dealerappnative.ui.components

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import java.io.File
import java.util.UUID

/** The existing add-photo tile, now with Camera / Gallery. Storage stays visit-owned. */
@Composable
internal fun VisitPhotoAdder(session: AgentVisitSession, onAddPhotos: (List<String>) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
    val currentSession by rememberUpdatedState(session)
    val currentAdd by rememberUpdatedState(onAddPhotos)
    var showSources by rememberSaveable(session.startedAt) { mutableStateOf(false) }
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
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(6)) { uris ->
        scope.launch {
            val readable = withContext(Dispatchers.IO) {
                uris.take((6 - currentSession.photoUris.size).coerceAtLeast(0)).mapNotNull { uri ->
                    runCatching {
                        val persisted = runCatching {
                            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }.isSuccess
                        if (persisted) uri.toString() else {
                            val directory = File(context.filesDir, "visit_photos").apply { mkdirs() }
                            val file = File(directory, "${UUID.randomUUID()}.jpg")
                            try {
                                requireNotNull(context.contentResolver.openInputStream(uri)).use { input ->
                                    file.outputStream().use { output -> input.copyTo(output) }
                                }
                                Uri.fromFile(file).toString()
                            } catch (error: Exception) { file.delete(); throw error }
                        }
                    }.getOrNull()
                }
            }
            if (currentSession.isActive) currentAdd(readable)
            if (uris.isNotEmpty() && readable.isEmpty())
                Toast.makeText(context, "Не удалось добавить фото. Попробуйте другое изображение.", Toast.LENGTH_LONG).show()
        }
    }
    Column(Modifier.size(96.dp).softCardPress(enabled = !preparing && pendingCamera == null) {
        keyboard?.hide(); showSources = true
    }.clip(RoundedCornerShape(12.dp)).border(1.dp, DealerDesign.divider, RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        DealerIcon(R.drawable.ic_visit_camera, DealerDesign.accent, 24.dp)
        Spacer(Modifier.height(6.dp))
        Text("Добавить фото", style = AgentVisitDesign.detail, color = DealerDesign.accent)
    }
    if (showSources && session.isActive && session.photoUris.size < 6) VisitPhotoSourceSheet(
        onDismiss = { showSources = false }, onGallery = {
            showSources = false
            runCatching { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                .onFailure { Toast.makeText(context, "Не удалось открыть галерею", Toast.LENGTH_SHORT).show() }
        }, onCamera = {
            showSources = false
            preparing = true
            scope.launch {
                var uri: Uri? = null
                try {
                    uri = withContext(Dispatchers.IO) { VisitCameraPhotos.create(context) }
                    if (currentSession.isActive && currentSession.photoUris.size < 6) {
                        pendingCamera = uri.toString()
                        camera.launch(uri)
                    } else withContext(Dispatchers.IO) { VisitCameraPhotos.discard(context, uri) }
                } catch (error: Exception) {
                    pendingCamera = null
                    uri?.let { withContext(Dispatchers.IO) { VisitCameraPhotos.discard(context, it) } }
                    Toast.makeText(context, "Не удалось открыть камеру", Toast.LENGTH_LONG).show()
                } finally { preparing = false }
            }
        })
}

/** Explicit, temporary per-file grants also work with camera apps on older Android versions. */
internal class VisitTakePictureContract : ActivityResultContracts.TakePicture() {
    override fun createIntent(context: Context, input: Uri): Intent = super.createIntent(context, input).apply {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        clipData = ClipData.newRawUri("Visit photo", input)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VisitPhotoSourceSheet(onDismiss: () -> Unit, onCamera: () -> Unit, onGallery: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Добавить фото", style = AgentVisitDesign.sheetTitle, color = DealerDesign.text,
                modifier = Modifier.padding(bottom = 4.dp))
            ReturnPrimaryButton("Сделать фото", onClick = onCamera)
            Box(Modifier.fillMaxWidth().height(48.dp).softCardPress(onClick = onGallery)
                .clip(RoundedCornerShape(12.dp)).background(DealerDesign.background), contentAlignment = Alignment.Center) {
                Text("Выбрать из галереи", style = AgentVisitDesign.button, color = DealerDesign.text)
            }
        }
    }
}
