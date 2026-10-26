package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.example.dealerappnative.data.storeForVisit
import com.example.dealerappnative.model.VisitStore
import com.example.dealerappnative.R
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun HistoryHeader(title: String, onBack: () -> Unit, action: (@Composable () -> Unit)? = null) {
    Box(Modifier.fillMaxWidth().height(56.dp).background(Color.White), contentAlignment = Alignment.Center) {
        Text(title, style = AgentVisitDesign.screenTitle, color = DealerDesign.text,
            maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 56.dp))
        SoftIconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)
            .size(48.dp).semantics { contentDescription = "Назад" }) {
            DealerIcon(R.drawable.ic_arrow_back, DealerDesign.text)
        }
        if (action != null) Box(Modifier.align(Alignment.CenterEnd).padding(end = 4.dp)) { action() }
    }
}

@Composable
internal fun HistorySurface(modifier: Modifier = Modifier, verticalPadding: Dp = 16.dp, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxWidth().softCardPress(onClick = onClick)
        .clip(RoundedCornerShape(12.dp)).background(Color.White)
        .padding(horizontal = 16.dp, vertical = verticalPadding), content = content)
}

@Composable
internal fun HistoryStoreSummary(visit: VisitStore, showId: Boolean = false) {
    val store = storeForVisit(visit)
    val actions = LocalStoreActions.current
    HistorySurface {
        Row(Modifier.heightIn(min = 40.dp), verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_store)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(visit.name, style = AgentVisitDesign.name, color = DealerDesign.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(if (showId) store.id else visit.address, style = AgentVisitDesign.detail, color = DealerDesign.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HistoryStoreAction("Открыть карту", R.drawable.ic_route, Modifier.weight(1f)) { actions.openMap(store) }
            HistoryStoreAction("Позвонить", R.drawable.ic_phone_call, Modifier.weight(1f)) { actions.dial(store.phoneNumber) }
        }
    }
}

@Composable
private fun HistoryStoreAction(title: String, icon: Int, modifier: Modifier, onClick: () -> Unit) {
    Row(modifier.softCardPress(onClick = onClick).height(40.dp).clip(RoundedCornerShape(12.dp)).background(DealerDesign.background).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        DealerIcon(icon); Spacer(Modifier.width(8.dp))
        Text(title, style = AgentVisitDesign.button, color = DealerDesign.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
internal fun HistoryValue(label: String, value: String, color: Color = DealerDesign.text) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Text(label, style = AgentVisitDesign.detail, color = DealerDesign.secondary, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(16.dp))
        Text(value, style = AgentVisitDesign.detail, color = color, maxLines = 2,
            modifier = Modifier.widthIn(max = 188.dp))
    }
}
