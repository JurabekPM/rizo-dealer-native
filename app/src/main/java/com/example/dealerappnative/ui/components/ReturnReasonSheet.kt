package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Same pattern as visit completion, with a comment always visible and additional notes allowed for every reason. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReturnReasonSheet(reasons: List<ReturnReason>, initialId: String?, initialComment: String,
    onDismiss: () -> Unit, onConfirm: (String, String) -> Unit, onManage: (() -> Unit)? = null, confirmLabel: String = "Подтвердить") {
    var selected by rememberSaveable { mutableStateOf(initialId?.takeIf { id -> id == otherReturnReasonId || reasons.any { it.id == id && it.enabled } }) }
    var comment by rememberSaveable { mutableStateOf(initialComment) }
    val focus = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), containerColor = Color.White,
        dragHandle = { Box(Modifier.padding(top = 12.dp, bottom = 8.dp).size(36.dp, 4.dp)
            .background(DealerDesign.divider, RoundedCornerShape(2.dp))) }) {
        Column(Modifier.fillMaxWidth().heightIn(max = 680.dp).imePadding()) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Причина возврата", style = AgentVisitDesign.sheetTitle, color = DealerDesign.text, modifier = Modifier.weight(1f))
                if (onManage != null) SoftTextButton(onClick = onManage) {
                    Text("Настроить", style = AgentVisitDesign.button, color = DealerDesign.accent)
                }
            }
            Text("Выберите причину. Ниже можно добавить свой комментарий.", style = AgentVisitDesign.detail,
                color = DealerDesign.secondary, modifier = Modifier.padding(horizontal = 16.dp).padding(top = 8.dp, bottom = 12.dp))
            Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp).selectableGroup()) {
                reasons.filter { it.enabled }.forEach { reason ->
                    Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).selectable(selected == reason.id, role = Role.RadioButton) {
                        focus.clearFocus(); keyboard?.hide(); selected = reason.id
                    }, verticalAlignment = Alignment.CenterVertically) {
                        Text(reason.label, style = AgentVisitDesign.name, color = DealerDesign.text, modifier = Modifier.weight(1f).padding(vertical = 8.dp))
                        RadioButton(selected == reason.id, onClick = null, colors = RadioButtonDefaults.colors(
                            selectedColor = DealerDesign.accent, unselectedColor = DealerDesign.tertiary))
                    }
                    HorizontalDivider(color = DealerDesign.divider)
                }
                Spacer(Modifier.height(12.dp))
                val other = selected == otherReturnReasonId
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(if (other) Color(0xFFFAF5FF) else DealerDesign.background)
                    .border(1.dp, if (other) DealerDesign.accent.copy(alpha = .24f) else Color.Transparent, RoundedCornerShape(12.dp))) {
                    Row(Modifier.fillMaxWidth().height(44.dp).padding(start = 12.dp, end = 8.dp)
                        .selectable(other, role = Role.RadioButton) { selected = otherReturnReasonId }, verticalAlignment = Alignment.CenterVertically) {
                        Text("Другое", style = AgentVisitDesign.emphasis, color = DealerDesign.text, modifier = Modifier.weight(1f))
                        RadioButton(other, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = DealerDesign.accent))
                    }
                    VisitCommentField(comment, onChange = { comment = it; if (selected == null) selected = otherReturnReasonId },
                        onFocus = { if (selected == null) selected = otherReturnReasonId }, placeholder = "Добавьте комментарий к возврату")
                }
                Spacer(Modifier.height(8.dp))
            }
            AgentVisitAction(confirmLabel, enabled = canConfirmReturnReason(selected, comment, reasons)) {
                selected?.let { focus.clearFocus(); keyboard?.hide(); onConfirm(it, comment.trim()) }
            }
        }
    }
}
