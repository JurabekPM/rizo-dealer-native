package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.ProfileDesign as Design
import kotlinx.coroutines.launch

private val personSaver = Saver<DealerPerson, String>(save = { encodeDealerPerson(it) }, restore = { decodeDealerPerson(it) })

/** Existing people open as text details; only the pencil enables the form. */
@Composable
internal fun AdminPersonEditScreen(initial: DealerPerson, people: List<DealerPerson>, dealerName: String,
    preferences: DealerProfilePreferences, onBack: () -> Unit, onSaved: (List<DealerPerson>) -> Unit) {
    val isNew = people.none { it.id == initial.id }
    var savedPerson by rememberSaveable(initial.id, stateSaver = personSaver) { mutableStateOf(initial) }
    var draft by rememberSaveable(initial.id, stateSaver = personSaver) { mutableStateOf(initial) }
    var editing by rememberSaveable(initial.id) { mutableStateOf(isNew) }
    var error by rememberSaveable(initial.id) { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val viewList = rememberLazyListState()
    val editList = rememberLazyListState()
    val focus = LocalFocusManager.current
    fun back() {
        if (saving) return
        focus.clearFocus()
        if (editing && !isNew) { draft = savedPerson; editing = false; error = null } else onBack()
    }
    BackHandler(onBack = ::back)
    Column(Modifier.fillMaxSize().background(DealerDesign.background).imePadding()) {
        ProfilePageHeader(if (editing) (if (isNew) "Новый сотрудник" else "Редактирование") else "Данные сотрудника",
            onBack = ::back, onEdit = if (editing) null else ({ draft = savedPerson; error = null; editing = true }))
        Box(Modifier.weight(1f)) {
            if (!editing) AdminPersonDetailsScreen(savedPerson, dealerName, viewList)
            else AdminPersonalProfileScreen(draft.profile, dealerName, editList,
                onChange = { draft = draft.copy(profile = it); error = null }, role = draft.role,
                onRoleChange = if (draft.isCurrentAdministrator) null else ({ role -> draft = draft.copy(role = role); error = null }))
        }
        if (editing) Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            error?.let { Text(it, style = Design.body, color = Color(0xFFE45555)) }
            DealerProfileButton(if (saving) "Сохраняем…" else "Сохранить", enabled = !saving) {
                focus.clearFocus()
                val candidate = upsertDealerPerson(people, draft)
                error = dealerPeopleError(candidate)
                if (error == null) {
                    saving = true
                    scope.launch {
                        val success = preferences.savePeople(candidate)
                        saving = false
                        if (success) {
                            val saved = preferences.readPeople()
                            savedPerson = saved.first { it.id == draft.id }
                            draft = savedPerson
                            editing = false
                            onSaved(saved)
                        } else error = "Не удалось сохранить сотрудника"
                    }
                }
            }
        }
    }
}
