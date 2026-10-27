package com.example.dealerappnative.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.AgentVisitSession
import com.example.dealerappnative.model.formatVisitElapsed
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** The timer recomposes only this small block, not the shared-transition/list tree. */
@Composable
internal fun AgentVisitTiming(session: AgentVisitSession) {
    val now by produceState(System.currentTimeMillis(), session.startedAt, session.finishedAt) {
        while (session.isActive) { value = System.currentTimeMillis(); delay(1000) }
    }
    val seconds = session.elapsedMillis(now) / 1000
    val elapsed = formatVisitElapsed(session.elapsedMillis(now))
    val date = remember { SimpleDateFormat("dd.MM.yyyy | HH:mm", Locale.getDefault()) }
    val finished = !session.isActive
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).padding(16.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.width(8.dp).height(if (finished) 92.dp else 40.dp)) {
                Box(Modifier.align(Alignment.TopCenter).padding(top = 4.dp).size(8.dp)
                    .background(DealerDesign.accent, CircleShape))
                if (finished) {
                    Box(Modifier.align(Alignment.TopCenter).padding(top = 14.dp).width(2.dp).height(40.dp)
                        .background(DealerDesign.accent, RoundedCornerShape(1.dp)))
                    Box(Modifier.align(Alignment.TopCenter).padding(top = 56.dp).size(8.dp)
                        .background(Color(0xFF22C348), CircleShape))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                VisitDateLine("Дата визита", date.format(Date(session.startedAt)),
                    if (finished) (if (session.isAttempt) "Попытка" else "Завершено") else elapsed,
                    if (finished) Color(0xFF22A447) else Color(0xFF2563EB))
                session.finishedAt?.let {
                    VisitDateLine("Дата завершения", date.format(Date(it)),
                        if (seconds >= 60) "${seconds / 60} мин" else "$seconds сек", DealerDesign.secondary)
                }
            }
        }
        session.reason?.let {
            Spacer(Modifier.height(12.dp))
            Text(it.label, style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                modifier = Modifier.padding(start = 20.dp))
        }
    }
}

@Composable
private fun VisitDateLine(label: String, value: String, trailing: String, tint: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = AgentVisitDesign.name, color = DealerDesign.text, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            Text(trailing, style = AgentVisitDesign.detail, color = tint)
        }
        Text(value, style = AgentVisitDesign.detail, color = DealerDesign.secondary)
    }
}

@Composable
internal fun VisitCommentField(value: String, onChange: (String) -> Unit, readOnly: Boolean = false,
    placeholder: String = "Введите комментарий", onFocus: () -> Unit = {}) {
    BasicTextField(value, onValueChange = onChange, readOnly = readOnly,
        textStyle = AgentVisitDesign.name.copy(color = DealerDesign.text),
        cursorBrush = SolidColor(DealerDesign.accent), minLines = 1, maxLines = 5,
        modifier = Modifier.fillMaxWidth().onFocusChanged { if (it.isFocused) onFocus() }
            .clip(RoundedCornerShape(12.dp)).background(DealerDesign.background).heightIn(min = 48.dp).padding(12.dp)
            .semantics { contentDescription = "Комментарий к визиту" },
        decorationBox = { inner ->
            Box { if (value.isEmpty()) Text(if (readOnly) "Без комментария" else placeholder,
                style = AgentVisitDesign.name, color = DealerDesign.tertiary); inner() }
        })
}

@Composable
internal fun AgentVisitNotes(session: AgentVisitSession, onComment: (String) -> Unit,
    onAddPhotos: (List<String>) -> Unit, onRemovePhoto: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (session.isActive && session.photoUris.size < 6) item(key = "add") {
                    VisitPhotoAdder(session, onAddPhotos)
                }
                items(session.photoUris, key = { it }) { uri ->
                    VisitPhoto(uri, session.isActive, onRemove = { onRemovePhoto(uri) })
                }
                if (!session.isActive && session.photoUris.isEmpty()) item {
                    Text("Фото не добавлены", style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                }
            }
        }
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Комментарии", style = AgentVisitDesign.detail, color = DealerDesign.text)
            if (session.isActive) VisitCommentField(session.comment, onComment)
            else Text(session.comment.ifBlank { "Без комментария" }, style = AgentVisitDesign.name,
                color = if (session.comment.isBlank()) DealerDesign.secondary else DealerDesign.text)
        }
    }
}

@Composable
private fun VisitPhoto(uri: String, removable: Boolean, onRemove: () -> Unit) {
    val context = LocalContext.current
    val resolver = context.contentResolver
    val bitmap by produceState<ImageBitmap?>(null, uri) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val source = Uri.parse(uri)
                if (Build.VERSION.SDK_INT >= 29 && source.scheme == "content")
                    resolver.loadThumbnail(source, Size(256, 256), null).asImageBitmap()
                else {
                    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    openVisitPhoto(context, source)?.use { BitmapFactory.decodeStream(it, null, bounds) }
                    val options = BitmapFactory.Options().apply {
                        inSampleSize = 1
                        while (bounds.outWidth / inSampleSize > 512 || bounds.outHeight / inSampleSize > 512) inSampleSize *= 2
                    }
                    openVisitPhoto(context, source)?.use { BitmapFactory.decodeStream(it, null, options)?.asImageBitmap() }
                }
            }.getOrNull()
        }
    }
    Box(Modifier.size(96.dp).clip(RoundedCornerShape(12.dp)).background(DealerDesign.background)) {
        bitmap?.let { Image(it, "Фото визита", Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
            ?: Text("Фото недоступно", style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                modifier = Modifier.align(Alignment.Center).padding(8.dp))
        if (removable) SoftIconButton(onClick = onRemove, modifier = Modifier.align(Alignment.TopEnd)
            .size(40.dp).background(Color.White.copy(alpha = .9f), RoundedCornerShape(bottomStart = 12.dp))
            .semantics { contentDescription = "Убрать фото" }) {
            DealerIcon(R.drawable.ic_agent_clear, DealerDesign.text, 20.dp)
        }
    }
}

@Composable
internal fun AgentVisitAction(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
        Box(Modifier.fillMaxWidth().height(48.dp).softCardPress(enabled = enabled, onClick = onClick)
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) DealerDesign.accent else DealerDesign.accent.copy(alpha = .35f)), contentAlignment = Alignment.Center) {
            Text(label, style = AgentVisitDesign.button, color = Color.White)
        }
    }
}

/** Packaged fictional samples and user-selected photos use the same bounded decoder. */
private fun openVisitPhoto(context: Context, uri: Uri): java.io.InputStream? =
    if (uri.scheme == "asset") context.assets.open(uri.path.orEmpty().removePrefix("/"))
    else context.contentResolver.openInputStream(uri)
