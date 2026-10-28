package com.example.dealerappnative.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.AdminPersonalProfile
import com.example.dealerappnative.model.DealerPerson
import java.util.UUID
import com.example.dealerappnative.model.DealerProfile
import com.example.dealerappnative.model.validationError
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.ProfileDesign as Design
import com.example.dealerappnative.ui.theme.DealerDesign
import kotlinx.coroutines.launch

private val dealerDraftSaver = Saver<DealerProfile, String>(save = { encodeDealerProfile(it) }, restore = { decodeDealerProfile(it) })
private val peopleSaver = Saver<List<DealerPerson>, String>(save = { encodeDealerPeople(it) }, restore = { decodeDealerPeople(it, AdminPersonalProfile()) })

/** People save individually; the dealer form keeps its own unsaved draft. */
@Composable
internal fun AdminProfileScreen(onBack: () -> Unit, onAdministratorChanged: (AdminPersonalProfile) -> Unit) {
    val context = LocalContext.current
    val preferences = remember(context) { DealerProfilePreferences(context) }
    var savedDealer by rememberSaveable(stateSaver = dealerDraftSaver) { mutableStateOf(preferences.read()) }
    var dealer by rememberSaveable(stateSaver = dealerDraftSaver) { mutableStateOf(savedDealer) }
    var editingDealer by rememberSaveable { mutableStateOf(false) }
    var people by rememberSaveable(stateSaver = peopleSaver) { mutableStateOf(preferences.readPeople()) }
    var selectedPersonId by rememberSaveable { mutableStateOf<String?>(null) }
    var newPersonId by rememberSaveable { mutableStateOf<String?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    val pager = rememberPagerState(pageCount = { 2 })
    val personalList = rememberLazyListState()
    val dealerList = rememberLazyListState()
    val dealerViewList = rememberLazyListState()
    val focus = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    var mapOpen by rememberSaveable { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var imageBusy by remember { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = people.firstOrNull { it.id == selectedPersonId }
        ?: newPersonId?.let { DealerPerson(it, AdminPersonalProfile(name = "", phone = "+998"), "Торговый агент") }
    if (selected != null) {
        AdminPersonEditScreen(selected, people, preferences.read().name, preferences,
            onBack = { selectedPersonId = null; newPersonId = null }, onSaved = {
                people = it
                onAdministratorChanged(it.first { person -> person.isCurrentAdministrator }.profile)
                selectedPersonId = selected.id; newPersonId = null
            })
        return
    }
    if (mapOpen) {
        DealerProfileLocationScreen(dealer, onBack = { mapOpen = false }, onSelect = {
            dealer = dealer.copy(address = it.address, latitude = it.latitude, longitude = it.longitude)
            error = null
            mapOpen = false
        })
        return
    }
    fun back() {
        if (saving) return
        focus.clearFocus()
        if (pager.currentPage == 1 && editingDealer) { dealer = savedDealer; editingDealer = false; error = null }
        else onBack()
    }
    BackHandler(onBack = ::back)
    Column(Modifier.fillMaxSize().background(DealerDesign.background).imePadding()) {
        ProfilePageHeader(if (pager.currentPage == 1 && editingDealer) "Редактирование" else "Профиль", onBack = ::back,
            onEdit = if (pager.currentPage == 1 && !editingDealer) ({ dealer = savedDealer; error = null; editingDealer = true }) else null)
        Box(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp).padding(top = 4.dp, bottom = 10.dp)) {
            AdminProfileTabs(pager, enabled = !saving)
        }
        HorizontalPager(pager, modifier = Modifier.weight(1f), userScrollEnabled = !saving, beyondViewportPageCount = 1) { page ->
            if (page == 0) AdminPeopleScreen(people, query, { query = it }, personalList,
                onPersonClick = { selectedPersonId = it }, onAdd = { newPersonId = "person-${UUID.randomUUID()}" })
            else if (!editingDealer) DealerProfileDetailsScreen(savedDealer, dealerViewList)
            else DealerProfileScreen(dealer, dealerList, onChange = { dealer = it; error = null },
                onOpenMap = { mapOpen = true }, onImageBusyChange = { imageBusy = it })
        }
        if (pager.currentPage == 1 && editingDealer) Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            error?.let { Text(it, style = Design.body, color = Color(0xFFE45555)) }
            DealerProfileButton(if (saving) "Сохраняем…" else "Сохранить", !saving && !imageBusy) {
                error = dealer.validationError()
                if (error == null) {
                    val dealerSnapshot = dealer
                    saving = true
                    scope.launch {
                        val saved = preferences.save(dealerSnapshot)
                        saving = false
                        if (saved) {
                            savedDealer = preferences.read()
                            dealer = savedDealer
                            editingDealer = false
                            Toast.makeText(context, "Данные сохранены", Toast.LENGTH_SHORT).show()
                        }
                        else error = "Не удалось сохранить данные. Попробуйте ещё раз"
                    }
                }
            }
        }
    }
}
