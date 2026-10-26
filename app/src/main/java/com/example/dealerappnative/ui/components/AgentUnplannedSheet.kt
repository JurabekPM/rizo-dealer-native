package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.UnplannedVisitKind
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Real-app choice sheet; Figma's Unplanned Visit store picker follows Continue. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AgentUnplannedSheet(onDismiss: () -> Unit, onContinue: (UnplannedVisitKind) -> Unit) {
    var selected by rememberSaveable { mutableStateOf<UnplannedVisitKind?>(null) }
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { Box(Modifier.padding(top = 10.dp, bottom = 16.dp).size(36.dp, 4.dp)
            .background(DealerDesign.divider, RoundedCornerShape(2.dp))) }) {
        Text("Внеплановые", style = AgentVisitDesign.sheetTitle, color = DealerDesign.text,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 20.dp))
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp).selectableGroup()) {
            UnplannedVisitKind.entries.forEachIndexed { index, kind ->
                Row(Modifier.fillMaxWidth().height(60.dp).selectable(selected == kind,
                    interactionSource = null, indication = null, role = Role.RadioButton,
                    onClick = { selected = kind }), verticalAlignment = Alignment.CenterVertically) {
                    DealerIcon(when (kind) {
                        UnplannedVisitKind.Visit -> R.drawable.ic_agent_history
                        UnplannedVisitKind.Request -> R.drawable.ic_visit
                        UnplannedVisitKind.Collection -> R.drawable.img_visit_collection_result
                    }, if (kind == UnplannedVisitKind.Collection) Color.Unspecified else DealerDesign.secondary)
                    Spacer(Modifier.width(16.dp))
                    Text(kind.label, style = AgentVisitDesign.emphasis, color = DealerDesign.text,
                        modifier = Modifier.weight(1f))
                    RadioButton(selected == kind, onClick = null, colors = RadioButtonDefaults.colors(
                        selectedColor = DealerDesign.accent, unselectedColor = DealerDesign.tertiary))
                }
                if (index < UnplannedVisitKind.entries.lastIndex) HorizontalDivider(
                    color = DealerDesign.divider, modifier = Modifier.padding(start = 40.dp))
            }
        }
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ChoiceAction("Отмена", Modifier.weight(1f), enabled = true, primary = false, onClick = onDismiss)
            ChoiceAction("Продолжить", Modifier.weight(1f), enabled = selected != null, primary = true) {
                selected?.let(onContinue)
            }
        }
    }
}

@Composable
private fun ChoiceAction(label: String, modifier: Modifier, enabled: Boolean, primary: Boolean, onClick: () -> Unit) {
    Box(modifier.height(48.dp).softCardPress(enabled = enabled, onClick = onClick)
        .background(if (enabled && primary) DealerDesign.accent else DealerDesign.background, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center) {
        Text(label, style = AgentVisitDesign.button, color = when {
            !enabled -> DealerDesign.secondary
            primary -> Color.White
            else -> DealerDesign.text
        })
    }
}
