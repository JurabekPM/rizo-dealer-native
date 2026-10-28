package com.example.dealerappnative.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.retainDealerProfileImage
import com.example.dealerappnative.model.AdminPersonalProfile
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.ProfileDesign as Design
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

/** Mounted by the editor, not by a recyclable lazy row. Selection changes the draft only. */
@Composable
internal fun rememberPersonPhotoPicker(personId: String, onSelected: (String) -> Unit,
    onBusyChange: (Boolean) -> Unit, onError: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val selected by rememberUpdatedState(onSelected)
    val busy by rememberUpdatedState(onBusyChange)
    val error by rememberUpdatedState(onError)
    val currentId by rememberUpdatedState(personId)
    var owner by rememberSaveable { mutableStateOf<String?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val target = owner
        owner = null
        if (uri == null || target != currentId) busy(false)
        else {
            busy(true)
            scope.launch {
                try {
                    if (retainDealerProfileImage(context, uri)) {
                        if (target == currentId) selected(uri.toString())
                    } else error("Выберите изображение размером до 5 МБ")
                } finally { busy(false) }
            }
        }
    }
    return {
        if (owner == null) {
            owner = personId
            busy(true)
            runCatching { launcher.launch(arrayOf("image/*")) }.onFailure {
                owner = null; busy(false); error("Не удалось открыть изображения")
            }
        }
    }
}

@Composable
internal fun PersonPhotoField(profile: AdminPersonalProfile, busy: Boolean, onSelect: () -> Unit) {
    DealerProfileSection("Фотография") {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileAvatar(R.drawable.ic_person_outline, 64.dp, imageUri = profile.photoUri, initials = profileInitials(profile.name))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                SoftTextButton(onSelect, enabled = !busy) {
                    Text(if (busy) "Проверяем фото…" else if (profile.photoUri.isBlank()) "Добавить фото" else "Изменить фото",
                        style = Design.value, color = DealerDesign.accent)
                }
                Text("Изображение до 5 МБ", style = Design.label, color = Design.secondary, modifier = Modifier.padding(start = 12.dp))
            }
        }
    }
}
