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
import com.example.dealerappnative.ui.theme.StatementDesign as Design
import com.example.dealerappnative.ui.theme.DealerDesign
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

internal fun statementDate(time: Long): String = SimpleDateFormat("dd.MM.yyyy · HH:mm", Locale.forLanguageTag("ru"))
    .apply { timeZone = TimeZone.getTimeZone("Asia/Tashkent") }.format(Date(time))

@Composable
internal fun StoreStatementCard(entry: StoreStatementEntry, clientName: String, expanded: Boolean,
    grouped: Boolean = false, lastInGroup: Boolean = true, onToggle: () -> Unit) {
    val date = remember(entry.occurredAt) { statementDate(entry.occurredAt) }
    val shortDate = remember(entry.occurredAt) { SimpleDateFormat("d MMM, HH:mm", Locale.forLanguageTag("ru"))
        .apply { timeZone = TimeZone.getTimeZone("Asia/Tashkent") }.format(Date(entry.occurredAt)) }
    val shape = if (grouped) RoundedCornerShape(bottomStart = if (lastInGroup) 12.dp else 0.dp,
        bottomEnd = if (lastInGroup) 12.dp else 0.dp) else RoundedCornerShape(12.dp)
    val rotation by animateFloatAsState(if (expanded) -90f else 90f, tween(280), label = "statement-chevron")
    val amountColor = Design.operationColor(entry.kind)
    Column(Modifier.fillMaxWidth().clip(shape).background(Color.White).animateContentSize(tween(280))) {
        Row(Modifier.fillMaxWidth().softCardPress(onClick = onToggle).padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(entry.title, style = Design.name, color = amountColor, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(12.dp))
                    Text(statementAmount(entry), style = Design.amount, color = amountColor, maxLines = 1)
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(entry.method, style = Design.caption, color = Design.secondary, maxLines = 1,
                        overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    Text((if (entry.dateLabel == "Дата визита") "Визит · " else "") + (if (grouped) shortDate else date),
                        style = Design.caption, color = Design.secondary, maxLines = 1)
                }
            }
            Spacer(Modifier.width(8.dp))
            Box(Modifier.rotate(rotation)) { DealerIcon(R.drawable.ic_chevron, Design.secondary, 16.dp) }
        }
        if (expanded) Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            HorizontalDivider(color = DealerDesign.divider)
            StatementInfo("Документ", entry.title)
            StatementInfo("Клиент", clientName)
            StatementInfo("Статус", entry.status)
            StatementInfo(entry.dateLabel, date)
            if (entry.kind == StatementKind.Request) Text("Заявка ещё не является проведённой продажей", style = Design.body, color = Design.secondary)
            if (entry.lines.isNotEmpty()) {
                HorizontalDivider(color = DealerDesign.divider)
                entry.lines.forEach { line ->
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(line.name, style = Design.body, color = Design.text)
                        Text("${line.quantity} шт × ${statementMoney(line.price, entry.currency)}", style = Design.body, color = Design.secondary)
                    }
                }
            }
            if (entry.comment.isNotBlank()) {
                Text("Комментарий", style = Design.label, color = Design.secondary)
                Text(entry.comment, style = Design.body, color = Design.text)
            }
        }
        if (grouped && !lastInGroup) HorizontalDivider(Modifier.padding(horizontal = 16.dp), color = DealerDesign.divider)
    }
}

@Composable
private fun StatementInfo(label: String, value: String, color: Color = Design.text) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(label, style = Design.body, color = Design.secondary, modifier = Modifier.weight(.8f))
        Spacer(Modifier.width(12.dp))
        Text(value, style = Design.body, color = color, modifier = Modifier.weight(1.2f))
    }
}
