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
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign
import kotlinx.coroutines.launch

private val personSaver = Saver<DealerPerson, String>(save = { encodeDealerPerson(it) }, restore = { decodeDealerPerson(it) })

/** One person is a local transaction. Back leaves the entire directory unchanged. */
@Composable
internal fun AdminPersonEditScreen(initial: DealerPerson, people: List<DealerPerson>, dealerName: String,
    preferences: DealerProfilePreferences, onBack: () -> Unit, onSaved: (List<DealerPerson>) -> Unit) {
    var draft by rememberSaveable(initial.id, stateSaver = personSaver) { mutableStateOf(initial) }
    var error by rememberSaveable(initial.id) { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val list = rememberLazyListState()
    BackHandler { if (!saving) onBack() }
    Column(Modifier.fillMaxSize().background(DealerDesign.background).imePadding()) {
        ChildScreenHeader(if (people.any { it.id == initial.id }) "Данные сотрудника" else "Новый сотрудник", { if (!saving) onBack() })
        Box(Modifier.weight(1f)) {
            AdminPersonalProfileScreen(draft.profile, dealerName, list, onChange = { draft = draft.copy(profile = it); error = null },
                role = draft.role, onRoleChange = if (draft.isCurrentAdministrator) null else ({ role -> draft = draft.copy(role = role); error = null }))
        }
        Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            error?.let { Text(it, style = AgentVisitDesign.detail, color = Color(0xFFE45555)) }
            DealerProfileButton(if (saving) "Сохраняем…" else "Сохранить", enabled = !saving) {
                val candidate = upsertDealerPerson(people, draft)
                error = dealerPeopleError(candidate)
                if (error == null) {
                    saving = true
                    scope.launch {
                        val success = preferences.savePeople(candidate)
                        saving = false
                        if (success) onSaved(preferences.readPeople())
                        else error = "Не удалось сохранить сотрудника"
                    }
                }
            }
        }
    }
}
