package com.example.dealerappnative.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.model.StatisticsDates
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AgentHistoryDatePicker(from: Long?, to: Long?, onDismiss: () -> Unit,
    onApply: (Long, Long) -> Unit) {
    val today = StatisticsDates.today()
    var range by rememberSaveable { mutableStateOf(from != null && from != to) }
    val dates = remember(today) { object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= today
    } }
    val day = rememberDatePickerState(initialSelectedDateMillis = from ?: today, selectableDates = dates)
    val period = rememberDateRangePickerState(initialSelectedStartDateMillis = from,
        initialSelectedEndDateMillis = to, selectableDates = dates)
    val colors = DatePickerDefaults.colors(containerColor = Color.White,
        selectedDayContainerColor = DealerDesign.accent, selectedDayContentColor = Color.White,
        dayInSelectionRangeContainerColor = Color(0xFFF4E6FC), dayInSelectionRangeContentColor = DealerDesign.accent,
        todayContentColor = DealerDesign.accent, todayDateBorderColor = DealerDesign.accent)
    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(primary = DealerDesign.accent,
        surface = Color.White, onSurface = DealerDesign.text), typography = MaterialTheme.typography.copy(
        titleLarge = AgentVisitDesign.sheetTitle, titleMedium = AgentVisitDesign.screenTitle,
        bodyLarge = AgentVisitDesign.name, bodyMedium = AgentVisitDesign.detail, labelLarge = AgentVisitDesign.button)) {
        DatePickerDialog(onDismissRequest = onDismiss, colors = colors, shape = RoundedCornerShape(20.dp),
            confirmButton = { SoftTextButton(enabled = if (range) period.selectedStartDateMillis != null &&
                period.selectedEndDateMillis != null else day.selectedDateMillis != null,
                onClick = {
                    if (range) onApply(period.selectedStartDateMillis!!, period.selectedEndDateMillis!!)
                    else day.selectedDateMillis?.let { onApply(it, it) }
                }) { Text("Применить", style = AgentVisitDesign.button) } },
            dismissButton = { SoftTextButton(onClick = onDismiss) { Text("Отмена", style = AgentVisitDesign.name) } }) {
            val position by animateFloatAsState(if (range) 1f else 0f, tween(280), label = "history-date-tab")
            BoxWithConstraints(Modifier.padding(16.dp).fillMaxWidth().height(44.dp).clip(RoundedCornerShape(12.dp))
                .background(DealerDesign.background).padding(4.dp)) {
                val width = maxWidth / 2
                Box(Modifier.offset { IntOffset((width.toPx() * position).roundToInt(), 0) }
                    .width(width).fillMaxHeight().background(Color.White, RoundedCornerShape(9.dp)))
                Row(Modifier.fillMaxSize().selectableGroup()) {
                    listOf("Дата", "Период").forEachIndexed { index, label ->
                        Box(Modifier.weight(1f).fillMaxHeight().selectable(range == (index == 1), role = Role.Tab,
                            interactionSource = remember { MutableInteractionSource() }, indication = null,
                            onClick = { range = index == 1 }), contentAlignment = Alignment.Center) {
                            Text(label, style = AgentVisitDesign.status, color = DealerDesign.text)
                        }
                    }
                }
            }
            if (range) DateRangePicker(period, modifier = Modifier.fillMaxWidth().heightIn(max = 440.dp),
                colors = colors, title = null, showModeToggle = false)
            else DatePicker(day, colors = colors, title = null, showModeToggle = false)
        }
    }
}
