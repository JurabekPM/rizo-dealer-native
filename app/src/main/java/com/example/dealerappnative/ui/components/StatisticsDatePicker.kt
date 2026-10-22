package com.example.dealerappnative.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.model.StatisticsDateRange
import com.example.dealerappnative.model.StatisticsPeriod
import com.example.dealerappnative.ui.theme.AgentDesign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StatisticsDatePicker(period: StatisticsPeriod, anchor: Long,
    initialRange: StatisticsDateRange, today: Long, onDismiss: () -> Unit, onApply: (Long, Long) -> Unit) {
    val selectable = remember(today) { object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= today
    } }
    // Scope the Agent's Onest fonts and purple palette to the native calendar only.
    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(primary = AgentDesign.accent,
        onPrimary = Color.White, primaryContainer = AgentDesign.tint, onPrimaryContainer = AgentDesign.text,
        surface = Color.White, onSurface = AgentDesign.text),
        typography = MaterialTheme.typography.copy(titleLarge = AgentDesign.value, titleMedium = AgentDesign.screenTitle,
            headlineLarge = AgentDesign.value, headlineMedium = AgentDesign.value, headlineSmall = AgentDesign.value, titleSmall = AgentDesign.menuDetail,
            bodyLarge = AgentDesign.heroLabel, bodyMedium = AgentDesign.label,
            labelLarge = AgentDesign.menuDetail, labelMedium = AgentDesign.detail, labelSmall = AgentDesign.detail, bodySmall = AgentDesign.detail)) {
        val colors = DatePickerDefaults.colors(containerColor = Color.White,
            selectedDayContainerColor = AgentDesign.accent, selectedDayContentColor = Color.White,
            todayContentColor = AgentDesign.accent, todayDateBorderColor = AgentDesign.accent,
            dayInSelectionRangeContainerColor = AgentDesign.tint, dayInSelectionRangeContentColor = AgentDesign.accent)
        if (period == StatisticsPeriod.Range) {
            val state = rememberDateRangePickerState(initialSelectedStartDateMillis = initialRange.start.coerceAtMost(today),
                initialSelectedEndDateMillis = initialRange.end.coerceAtMost(today), selectableDates = selectable)
            DatePickerDialog(onDismissRequest = onDismiss, shape = RoundedCornerShape(20.dp), colors = colors,
                confirmButton = { SoftTextButton(enabled = state.selectedStartDateMillis != null && state.selectedEndDateMillis != null,
                    onClick = { onApply(state.selectedStartDateMillis!!, state.selectedEndDateMillis!!) }) {
                    Text("Применить", style = AgentDesign.menuTitle)
                } }, dismissButton = { SoftTextButton(onClick = onDismiss) { Text("Отмена", style = AgentDesign.menuTitle) } }) {
                DateRangePicker(state, modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp), colors = colors,
                    title = { Text("Выберите период", style = AgentDesign.screenTitle,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 12.dp)) }, showModeToggle = false)
            }
        } else {
            val state = rememberDatePickerState(initialSelectedDateMillis = anchor.coerceAtMost(today), selectableDates = selectable)
            DatePickerDialog(onDismissRequest = onDismiss, shape = RoundedCornerShape(20.dp), colors = colors,
                confirmButton = { SoftTextButton(enabled = state.selectedDateMillis != null,
                    onClick = { state.selectedDateMillis?.let { onApply(it, it) } }) { Text("Применить", style = AgentDesign.menuTitle) } },
                dismissButton = { SoftTextButton(onClick = onDismiss) { Text("Отмена", style = AgentDesign.menuTitle) } }) {
                DatePicker(state, colors = colors, title = { Text(when (period) {
                    StatisticsPeriod.Week -> "Выберите дату нужной недели"
                    StatisticsPeriod.Month -> "Выберите дату нужного месяца"
                    else -> "Выберите день"
                }, style = AgentDesign.menuDetail, modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 12.dp)) })
            }
        }
    }
}
