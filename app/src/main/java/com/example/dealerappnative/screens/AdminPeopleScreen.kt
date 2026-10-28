package com.example.dealerappnative.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.ProfileDesign as Design

@Composable
internal fun AdminPeopleScreen(people: List<DealerPerson>, query: String, onQueryChange: (String) -> Unit,
    listState: LazyListState, onPersonClick: (String) -> Unit, onAdd: () -> Unit) {
    val filtered = people.filter { person ->
        listOf(person.profile.name, person.profile.phone, displayAdminPhone(person.profile.phone), person.role).any { it.contains(query.trim(), true) }
    }
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp).padding(bottom = 8.dp)) {
            SearchField(query, onQueryChange, "Поиск сотрудников", height = 38.dp, textStyle = Design.value)
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Сотрудники · ${filtered.size}", style = Design.label, color = Design.secondary, modifier = Modifier.weight(1f))
                Row(Modifier.height(40.dp).clip(RoundedCornerShape(10.dp)).softCardPress(onClick = onAdd)
                    .semantics { contentDescription = "Добавить сотрудника" }.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DealerIcon(R.drawable.ic_request_add, DealerDesign.accent, 18.dp)
                    Text("Добавить", style = Design.body, color = DealerDesign.accent)
                }
            }
        }
        LazyColumn(Modifier.weight(1f), state = listState, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered, key = { it.id }) { person ->
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White)
                    .softCardPress { onPersonClick(person.id) }.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    ProfileAvatar(R.drawable.ic_person_outline, 40.dp, initials = profileInitials(person.profile.name))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(person.profile.name, style = Design.name, color = Design.text, maxLines = 1,
                                overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                            if (person.isCurrentAdministrator) ProfileSelfBadge()
                        }
                        Text(person.role, style = Design.label, color = Design.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(displayAdminPhone(person.profile.phone), style = Design.body, color = Design.secondary)
                    }
                }
            }
            if (filtered.isEmpty()) item { Text("Сотрудники не найдены", style = Design.value, color = Design.secondary, modifier = Modifier.padding(16.dp)) }
        }
    }
}
