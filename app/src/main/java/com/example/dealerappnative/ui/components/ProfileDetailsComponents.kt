package com.example.dealerappnative.ui.components

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.dealerProfileThumbnail
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.ProfileDesign as Design

@Composable
internal fun ProfilePageHeader(title: String, onBack: () -> Unit, onEdit: (() -> Unit)? = null) {
    Box(Modifier.fillMaxWidth().height(56.dp).background(Color.White), contentAlignment = Alignment.Center) {
        Text(title, style = DealerDesign.title, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.align(Alignment.CenterStart).padding(horizontal = 56.dp))
        SoftIconButton(onBack, Modifier.align(Alignment.CenterStart).padding(start = 4.dp).size(48.dp).semantics { contentDescription = "Назад" }) {
            DealerIcon(R.drawable.ic_arrow_back, Design.text)
        }
        if (onEdit != null) SoftIconButton(onEdit, Modifier.align(Alignment.CenterEnd).padding(end = 4.dp).size(48.dp)
            .semantics { contentDescription = "Изменить данные" }) { DealerIcon(R.drawable.ic_edit, DealerDesign.accent, 22.dp) }
    }
}

internal fun profileInitials(name: String): String = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.take(2)
    .joinToString("") { it.take(1) }.uppercase()

@Composable
internal fun ProfileAvatar(@DrawableRes icon: Int, size: Dp = 48.dp, imageUri: String = "", initials: String? = null) {
    val context = LocalContext.current
    val image = if (imageUri.isBlank()) null else {
        val bitmap by produceState<Bitmap?>(null, imageUri) { value = dealerProfileThumbnail(context, imageUri) }
        bitmap
    }
    Box(Modifier.size(size).clip(CircleShape).background(Design.tint), contentAlignment = Alignment.Center) {
        when {
            image != null -> Image(image.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            !initials.isNullOrBlank() -> Text(initials, style = if (size > 48.dp) Design.title else Design.name, color = Design.mutedAccent)
            else -> DealerIcon(icon, Design.mutedAccent, if (size > 48.dp) 28.dp else 22.dp)
        }
    }
}

@Composable
internal fun ProfileSelfBadge() {
    Text("Вы", style = Design.badge, color = Design.mutedAccent,
        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Design.tint).padding(horizontal = 6.dp, vertical = 2.dp))
}

@Composable
internal fun ProfileIdentity(name: String, subtitle: String, @DrawableRes icon: Int, imageUri: String = "", initials: String? = null, self: Boolean = false) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        ProfileAvatar(icon, 56.dp, imageUri, initials)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(name, style = Design.title, color = Design.text, maxLines = 3, overflow = TextOverflow.Ellipsis)
            Text(subtitle, style = Design.body, color = Design.secondary)
            if (self) ProfileSelfBadge()
        }
    }
}

/** View mode has selectable text only: no disabled form fields or hidden mutation callbacks. */
@Composable
internal fun ProfileInfo(label: String, value: String) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = Design.label, color = Design.secondary)
        SelectionContainer { Text(value.ifBlank { "Не указано" }, style = Design.value, color = Design.text) }
    }
}

@Composable
internal fun ProfileImagePreview(label: String, uri: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(null, uri) { if (uri.isNotBlank()) value = dealerProfileThumbnail(context, uri) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = Design.label, color = Design.secondary)
        Box(Modifier.fillMaxWidth().height(88.dp).clip(RoundedCornerShape(10.dp)).background(Design.field), contentAlignment = Alignment.Center) {
            if (bitmap != null) Image(bitmap!!.asImageBitmap(), label, Modifier.fillMaxSize().padding(4.dp), contentScale = ContentScale.Fit)
            else Text("Не добавлено", style = Design.label, color = Design.secondary)
        }
    }
}
