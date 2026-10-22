package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.LocalVisitReturns
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun ReturnReasonSettingsScreen(onBack: () -> Unit) {
    val store = LocalVisitReturns.current.reasonSettings
    var label by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var editLabel by rememberSaveable { mutableStateOf("") }
    var editError by rememberSaveable { mutableStateOf<String?>(null) }
    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize().background(DealerDesign.background).imePadding()) {
        HistoryHeader("Причины возврата", onBack)
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { Text("Включённые причины видны агентам. Выключенные сохраняются и могут быть включены снова.",
                style = AgentVisitDesign.detail, color = DealerDesign.secondary, modifier = Modifier.padding(16.dp)) }
            listOf(true, false).forEach { system ->
                item(key = "title-$system") { Text(if (system) "Системные причины" else "Причины администратора",
                    style = AgentVisitDesign.emphasis, color = DealerDesign.text, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }
                item(key = "reasons-$system") { HistorySurface(verticalPadding = 0.dp) {
                    val reasons = store.reasons.filter { it.system == system }
                    reasons.forEachIndexed { index, reason ->
                        Row(Modifier.fillMaxWidth().heightIn(min = 64.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(reason.label, style = AgentVisitDesign.name, color = if (reason.enabled) DealerDesign.text else DealerDesign.secondary,
                                modifier = Modifier.weight(1f).padding(vertical = 12.dp))
                            if (!system) SoftIconButton(onClick = { editingId = reason.id; editLabel = reason.label; editError = null },
                                modifier = Modifier.size(44.dp).semantics { contentDescription = "Изменить причину: ${reason.label}" }) { DealerIcon(R.drawable.ic_edit, size = 20.dp) }
                            Switch(checked = reason.enabled, onCheckedChange = { store.setEnabled(reason.id, it) },
                                modifier = Modifier.semantics { contentDescription = reason.label }, colors = SwitchDefaults.colors(checkedTrackColor = DealerDesign.accent))
                        }
                        if (index < reasons.lastIndex) HorizontalDivider(color = DealerDesign.divider)
                    }
                    if (reasons.isEmpty()) Text("Пока нет добавленных причин", style = AgentVisitDesign.detail,
                        color = DealerDesign.secondary, modifier = Modifier.padding(vertical = 16.dp))
                } }
            }
            item { HistorySurface {
                Text("Новая причина", style = AgentVisitDesign.emphasis, color = DealerDesign.text)
                Spacer(Modifier.height(12.dp))
                ReturnReasonTextField(label, { label = it; error = null })
                error?.let { Text(it, style = AgentVisitDesign.detail, color = Color(0xFFE96666), modifier = Modifier.padding(top = 8.dp)) }
                Spacer(Modifier.height(8.dp))
                Box(Modifier.fillMaxWidth().height(48.dp).softCardPress(enabled = label.isNotBlank()) {
                    error = store.add(label); if (error == null) label = ""
                }.clip(RoundedCornerShape(12.dp)).background(if (label.isBlank()) DealerDesign.tertiary else DealerDesign.accent),
                    contentAlignment = Alignment.Center) { Text("Добавить причину", style = AgentVisitDesign.button, color = Color.White) }
            } }
        }
    }
    if (editingId != null) AlertDialog(onDismissRequest = { editingId = null }, containerColor = Color.White,
        title = { Text("Изменить причину", style = AgentVisitDesign.sheetTitle) },
        text = { Column { ReturnReasonTextField(editLabel, { editLabel = it; editError = null })
            editError?.let { Text(it, style = AgentVisitDesign.detail, color = Color(0xFFE96666)) } } },
        confirmButton = { SoftTextButton(onClick = { editError = store.rename(editingId!!, editLabel); if (editError == null) editingId = null }) {
            Text("Сохранить", style = AgentVisitDesign.button, color = DealerDesign.accent) } },
        dismissButton = { SoftTextButton(onClick = { editingId = null }) { Text("Отмена", style = AgentVisitDesign.button, color = DealerDesign.secondary) } })
}

@Composable
private fun ReturnReasonTextField(value: String, onChange: (String) -> Unit) {
    BasicTextField(value, onChange, singleLine = true, textStyle = AgentVisitDesign.name,
        cursorBrush = SolidColor(DealerDesign.accent), modifier = Modifier.fillMaxWidth().height(48.dp)
            .clip(RoundedCornerShape(12.dp)).background(DealerDesign.background).padding(horizontal = 12.dp)
            .semantics { contentDescription = "Название причины возврата" }, decorationBox = { inner ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                if (value.isBlank()) Text("Введите причину", style = AgentVisitDesign.name, color = DealerDesign.tertiary)
                inner()
            }
        })
}
