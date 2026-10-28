package com.example.dealerappnative.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign as Design
import com.example.dealerappnative.ui.theme.DealerDesign
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

internal fun statementDate(time: Long): String = SimpleDateFormat("dd.MM.yyyy · HH:mm", Locale.forLanguageTag("ru"))
    .apply { timeZone = TimeZone.getTimeZone("Asia/Tashkent") }.format(Date(time))

@Composable
internal fun StoreStatementCard(entry: StoreStatementEntry, clientName: String, expanded: Boolean, onToggle: () -> Unit) {
    val rotation by animateFloatAsState(if (expanded) -90f else 90f, tween(220), label = "statement-chevron")
    val amountColor = when (entry.kind.direction) { -1 -> Color(0xFFDC2626); 1 -> Color(0xFF15803D); else -> DealerDesign.text }
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).animateContentSize(tween(220))) {
        Row(Modifier.fillMaxWidth().softCardPress(onClick = onToggle).padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(entry.title, style = Design.name, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(12.dp))
                    Text(statementAmount(entry), style = Design.emphasis, color = amountColor, maxLines = 1)
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(entry.method, style = Design.detail, color = DealerDesign.secondary, maxLines = 1,
                        overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    Text((if (entry.dateLabel == "Дата визита") "Визит · " else "") + statementDate(entry.occurredAt),
                        style = DealerDesign.type(11, 15), color = DealerDesign.secondary, maxLines = 1)
                }
            }
            Spacer(Modifier.width(8.dp))
            Box(Modifier.rotate(rotation)) { DealerIcon(R.drawable.ic_chevron, DealerDesign.tertiary, 16.dp) }
        }
        if (expanded) Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            HorizontalDivider(color = DealerDesign.divider)
            StatementInfo("Клиент", clientName)
            StatementInfo("Статус", entry.status, if (entry.kind == StatementKind.Request) DealerDesign.secondary else Color(0xFF15803D))
            StatementInfo(entry.dateLabel, statementDate(entry.occurredAt))
            if (entry.kind == StatementKind.Request) Text("Заявка ещё не является проведённой продажей", style = Design.detail, color = DealerDesign.secondary)
            if (entry.lines.isNotEmpty()) {
                HorizontalDivider(color = DealerDesign.divider)
                entry.lines.forEach { line ->
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(line.name, style = Design.detail, color = DealerDesign.text)
                        Text("${line.quantity} шт × ${statementMoney(line.price, entry.currency)}", style = Design.detail, color = DealerDesign.secondary)
                    }
                }
            }
            if (entry.comment.isNotBlank()) {
                Text("Комментарий", style = Design.detail, color = DealerDesign.secondary)
                Text(entry.comment, style = Design.detail)
            }
        }
    }
}

@Composable
private fun StatementInfo(label: String, value: String, color: Color = DealerDesign.text) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(label, style = Design.detail, color = DealerDesign.secondary, modifier = Modifier.weight(.8f))
        Spacer(Modifier.width(12.dp))
        Text(value, style = Design.detail, color = color, modifier = Modifier.weight(1.2f))
    }
}
