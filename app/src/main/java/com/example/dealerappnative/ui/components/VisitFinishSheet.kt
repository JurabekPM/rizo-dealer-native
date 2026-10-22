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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.AgentVisitSession
import com.example.dealerappnative.model.VisitFinishReason
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VisitFinishSheet(session: AgentVisitSession, onComment: (String) -> Unit,
    onDismiss: () -> Unit, onFinish: (VisitFinishReason) -> Unit) {
    var selected by rememberSaveable { mutableStateOf<VisitFinishReason?>(null) }
    val focus = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), containerColor = Color.White,
        dragHandle = { Box(Modifier.padding(top = 12.dp, bottom = 8.dp).size(36.dp, 4.dp)
            .background(DealerDesign.divider, RoundedCornerShape(2.dp))) }) {
        Column(Modifier.fillMaxWidth().heightIn(max = 680.dp).imePadding()) {
            Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Завершение визита", style = AgentVisitDesign.sheetTitle, color = DealerDesign.text,
                    modifier = Modifier.weight(1f))
                SoftIconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)
                    .semantics { contentDescription = "Закрыть выбор причины" }) {
                    DealerIcon(R.drawable.ic_agent_clear, DealerDesign.secondary, 20.dp)
                }
            }
            Text("Операций не было. Выберите причину или оставьте свой комментарий.",
                style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp))
            Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp).selectableGroup()) {
                VisitFinishReason.entries.filter { it != VisitFinishReason.Other }.forEach { reason ->
                    Row(Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        .selectable(selected == reason, role = Role.RadioButton) {
                            focus.clearFocus(); keyboard?.hide(); selected = reason
                        }, verticalAlignment = Alignment.CenterVertically) {
                        Text(reason.label, style = AgentVisitDesign.name, color = DealerDesign.text,
                            modifier = Modifier.weight(1f))
                        RadioButton(selected == reason, onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = DealerDesign.accent,
                                unselectedColor = DealerDesign.tertiary))
                    }
                    HorizontalDivider(color = DealerDesign.divider)
                }
                Spacer(Modifier.height(12.dp))
                val other = selected == VisitFinishReason.Other
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(if (other) Color(0xFFFAF5FF) else DealerDesign.background)
                    .border(1.dp, if (other) DealerDesign.accent.copy(alpha = .24f) else Color.Transparent,
                        RoundedCornerShape(12.dp))) {
                    Row(Modifier.fillMaxWidth().height(44.dp).padding(start = 12.dp, end = 8.dp)
                        .selectable(other, role = Role.RadioButton) { selected = VisitFinishReason.Other },
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("Другое", style = AgentVisitDesign.emphasis, color = DealerDesign.text,
                            modifier = Modifier.weight(1f))
                        RadioButton(other, onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = DealerDesign.accent,
                                unselectedColor = DealerDesign.tertiary))
                    }
                    // Always visible: either focusing or editing selects Other.
                    VisitCommentField(session.comment, onChange = {
                        selected = VisitFinishReason.Other
                        onComment(it)
                    }, onFocus = { selected = VisitFinishReason.Other }, placeholder = "Опишите причину")
                }
                if (selected == VisitFinishReason.StoreClosed) Text("Сохраним как попытку визита: магазин закрыт.",
                    style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                    modifier = Modifier.padding(top = 12.dp))
                Spacer(Modifier.height(8.dp))
            }
            AgentVisitAction("Завершить визит", enabled = session.canFinish(selected)) {
                selected?.let { focus.clearFocus(); keyboard?.hide(); onFinish(it) }
            }
        }
    }
}
