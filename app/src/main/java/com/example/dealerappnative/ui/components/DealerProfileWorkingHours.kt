package com.example.dealerappnative.ui.components

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun DealerProfileWorkingHours(profile: DealerProfile, onChange: (DealerProfile) -> Unit) {
    DealerProfileSection("Рабочие часы") {
        SoftTextButton(onClick = { onChange(profile.applyWorkingHoursToAll()) }, enabled = profile.days.any { it.enabled },
            modifier = Modifier.align(Alignment.End)) {
            Text("Применить ко всем дням", style = AgentVisitDesign.detail, color = DealerDesign.accent)
        }
        profile.days.forEachIndexed { index, day ->
            if (index > 0) HorizontalDivider(color = DealerDesign.divider)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(dealerWeekdays[index], style = AgentVisitDesign.detail, color = DealerDesign.text)
                    if (!day.enabled) Text("Выходной", style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                    else if (day.closesAt < day.opensAt) Text("До следующего дня", style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                }
                if (day.enabled) {
                    DealerProfileTime(day.opensAt, "Открытие, ${dealerWeekdays[index]}") { value ->
                        onChange(profile.copy(days = profile.days.mapIndexed { i, d -> if (i == index) d.copy(opensAt = value) else d }))
                    }
                    Text("—", style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                    DealerProfileTime(day.closesAt, "Закрытие, ${dealerWeekdays[index]}") { value ->
                        onChange(profile.copy(days = profile.days.mapIndexed { i, d -> if (i == index) d.copy(closesAt = value) else d }))
                    }
                }
                Switch(checked = day.enabled, onCheckedChange = { enabled ->
                    onChange(profile.copy(days = profile.days.mapIndexed { i, d -> if (i == index) d.copy(enabled = enabled) else d }))
                }, colors = SwitchDefaults.colors(checkedTrackColor = DealerDesign.accent),
                    modifier = Modifier.semanticsForDay(dealerWeekdays[index]))
            }
        }
    }
}

private fun Modifier.semanticsForDay(day: String) = this.then(Modifier.semantics { contentDescription = "Рабочий день: $day" })

@Composable
private fun DealerProfileTime(value: Int, label: String, onChange: (Int) -> Unit) {
    val context = LocalContext.current
    Box(Modifier.width(52.dp).height(48.dp).clip(RoundedCornerShape(8.dp)).background(DealerDesign.background)
        .softCardPress { TimePickerDialog(context, { _, hour, minute -> onChange(hour * 60 + minute) }, value / 60, value % 60, true).show() }
        .semantics { contentDescription = "$label: ${dealerClock(value)}" }, contentAlignment = Alignment.Center) {
        Text(dealerClock(value), style = AgentVisitDesign.detail)
    }
}
