package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import com.example.dealerappnative.location.compactStoreAddress
import com.example.dealerappnative.model.StoreLocation
import com.example.dealerappnative.model.StoreRouteSummary
import com.example.dealerappnative.model.StoreUiModel
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Figma sheet surface/handle and 15/20 + 13/18 type; values belong to the selected destination. */
@Composable
internal fun StoreMapInfoSheet(store: StoreUiModel, location: StoreLocation,
    summary: StoreRouteSummary?, loading: Boolean, onEdit: (() -> Unit)?, saved: Boolean = false, showHandle: Boolean = true, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 8.dp, bottom = 16.dp)) {
        if (showHandle) {
            Box(Modifier.align(Alignment.CenterHorizontally).size(width = 32.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp)).background(DealerDesign.divider))
            Spacer(Modifier.height(16.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_store, DealerDesign.accent)
            Spacer(Modifier.width(12.dp))
            Text(store.name, style = AgentVisitDesign.screenTitle, color = DealerDesign.text,
                maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = DealerDesign.divider)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_pin)
            Spacer(Modifier.width(12.dp))
            Text(compactStoreAddress(location.address).ifBlank { "Адрес не указан" },
                style = AgentVisitDesign.name, color = DealerDesign.text, maxLines = 2,
                overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            if (onEdit != null) SoftIconButton(onClick = onEdit, modifier = Modifier.size(44.dp)
                .semantics { contentDescription = "Изменить локацию" }) {
                DealerIcon(R.drawable.ic_edit, DealerDesign.accent)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            if (summary != null) {
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    DealerIcon(R.drawable.ic_visit_route, size = 20.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(summary.distanceText, style = AgentVisitDesign.emphasis, color = DealerDesign.secondary,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.width(16.dp))
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    DealerIcon(R.drawable.ic_agent_history, size = 20.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(summary.durationText, style = AgentVisitDesign.emphasis, color = DealerDesign.secondary,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            } else Text(if (loading) "Прокладываем маршрут…" else "Маршрут недоступен. Проверьте интернет",
                style = AgentVisitDesign.detail, color = DealerDesign.secondary)
        }
        if (saved) {
            Spacer(Modifier.height(8.dp))
            Text("Локация сохранена", style = AgentVisitDesign.detail, color = Color(0xFF15803D))
        }
    }
}
