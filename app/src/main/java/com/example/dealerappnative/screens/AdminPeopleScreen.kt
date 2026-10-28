package com.example.dealerappnative.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun AdminPeopleScreen(people: List<DealerPerson>, query: String, onQueryChange: (String) -> Unit,
    listState: LazyListState, onEdit: (String) -> Unit, onAdd: () -> Unit) {
    val filtered = people.filter { person ->
        listOf(person.profile.name, person.profile.phone, displayAdminPhone(person.profile.phone), person.role).any { it.contains(query.trim(), true) }
    }
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp).padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SearchField(query, onQueryChange, "Имя, телефон или роль", height = 40.dp)
            Text("Сотрудники · ${filtered.size}", style = AgentVisitDesign.detail, color = DealerDesign.secondary)
        }
        LazyColumn(Modifier.weight(1f), state = listState, contentPadding = PaddingValues(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered, key = { it.id }) { person ->
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White)
                    .softCardPress { onEdit(person.id) }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).clip(CircleShape).background(DealerDesign.background), contentAlignment = Alignment.Center) {
                        DealerIcon(R.drawable.ic_person_outline, size = 24.dp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(person.profile.name, style = AgentVisitDesign.name, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                            if (person.isCurrentAdministrator) Text("  Вы", style = AgentVisitDesign.detail, color = DealerDesign.accent)
                        }
                        Text(person.role, style = AgentVisitDesign.detail, color = DealerDesign.accent)
                        Text(displayAdminPhone(person.profile.phone), style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                    }
                }
            }
            if (filtered.isEmpty()) item { Text("Сотрудники не найдены", style = AgentVisitDesign.name,
                color = DealerDesign.secondary, modifier = Modifier.padding(24.dp)) }
        }
        Box(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) { DealerProfileButton("Добавить сотрудника", onClick = onAdd) }
    }
}
