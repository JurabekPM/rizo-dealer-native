package com.example.dealerappnative.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Local prototype collection; saving an amount records work without finishing its visit. */
@Composable
internal fun AgentVisitCollectionScreen(visit: VisitStore, sessions: AgentVisitSessions, onBack: () -> Unit,
    onStart: (() -> Boolean)? = null) {
    val session = sessions.sessions[visit.id]
    val context = LocalContext.current
    var amount by rememberSaveable(visit.id) { mutableStateOf(session?.collectionAmount?.toString().orEmpty()) }
    val value = amount.toLongOrNull() ?: 0L
    val recorded = VisitResult.Collection in (session?.results ?: visit.results)
    BackHandler(onBack = onBack)
    if (recorded && session?.isActive == false) {
        val entry = AgentHistoryEntry(visit, session)
        AgentHistoryOperationScreen(entry, historyOperations(entry).first { it.result == VisitResult.Collection }, onBack)
        return
    }
    Column(Modifier.fillMaxSize().background(DealerDesign.background).imePadding()) {
        HistoryHeader("Инкассация", onBack)
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { HistoryStoreSummary(visit) }
            if (session?.isActive == true) item { HistorySurface {
                Text("Сумма инкассации", style = AgentVisitDesign.emphasis, color = DealerDesign.text)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(amount, onValueChange = { text ->
                    if (text.length <= 12 && text.all(Char::isDigit)) amount = text
                }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                    textStyle = AgentVisitDesign.emphasis, placeholder = { Text("0", style = AgentVisitDesign.emphasis) },
                    suffix = { Text("UZS", style = AgentVisitDesign.emphasis) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DealerDesign.accent, unfocusedBorderColor = DealerDesign.divider))
            } } else item { HistorySurface {
                Text(if (session == null) "Начните визит, чтобы оформить инкассацию" else "Визит завершён. Инкассации не было",
                    style = AgentVisitDesign.name, color = DealerDesign.secondary)
            } }
        }
        if (session == null) AgentVisitAction("Начать визит") {
            if (!(onStart?.invoke() ?: sessions.start(visit.id, visit.results)))
                Toast.makeText(context, "Сначала завершите текущий визит", Toast.LENGTH_SHORT).show()
        } else if (session.isActive) AgentVisitAction("Сохранить", enabled = canSaveVisitCollection(session, value)) {
            if (sessions.saveCollection(visit.id, value)) onBack()
        }
    }
}
