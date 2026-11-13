package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.DealerThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThemeSettingsSheet(
    mode: DealerThemeMode,
    onSelect: (DealerThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = DealerDesign.surface
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
            Text("Тема оформления", style = DealerDesign.title, modifier = Modifier.padding(bottom = 12.dp))
            Column(Modifier.fillMaxWidth().selectableGroup()) {
                DealerThemeMode.entries.forEach { option ->
                    val selected = mode == option
                    Row(
                        Modifier.fillMaxWidth().heightIn(min = 52.dp)
                            .background(if (selected) DealerDesign.softAccent else DealerDesign.surface, RoundedCornerShape(12.dp))
                            .selectable(
                                selected = selected, role = Role.RadioButton, interactionSource = null,
                                indication = SoftPressIndication, onClick = { onSelect(option) }
                            ).padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(option.label, style = DealerDesign.body, modifier = Modifier.weight(1f))
                        RadioButton(selected = selected, onClick = null)
                    }
                }
            }
        }
    }
}
