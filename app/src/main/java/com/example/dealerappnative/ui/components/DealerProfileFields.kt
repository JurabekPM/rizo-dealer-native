package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun DealerProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = AgentVisitDesign.emphasis)
        content()
    }
}

@Composable
internal fun DealerProfileField(label: String, value: String, onChange: (String) -> Unit, numeric: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = AgentVisitDesign.detail, color = DealerDesign.secondary)
        BasicTextField(value, onChange, textStyle = AgentVisitDesign.name.copy(color = DealerDesign.text),
            singleLine = true, cursorBrush = SolidColor(DealerDesign.accent),
            keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Number else KeyboardType.Text),
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).clip(RoundedCornerShape(12.dp))
                .background(DealerDesign.background).semantics { contentDescription = label },
            decorationBox = { inner -> Box(Modifier.padding(horizontal = 12.dp, vertical = 14.dp), contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) Text(label, style = AgentVisitDesign.name, color = DealerDesign.tertiary)
                inner()
            } })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DealerProfileChoice(label: String, value: String, options: List<String>, onSelect: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = AgentVisitDesign.detail, color = DealerDesign.secondary)
        Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).clip(RoundedCornerShape(12.dp))
            .background(DealerDesign.background).softCardPress { query = ""; open = true }.padding(12.dp)
            .semantics { contentDescription = "$label: $value" }, verticalAlignment = Alignment.CenterVertically) {
            Text(value.ifBlank { "Выбрать" }, style = AgentVisitDesign.name,
                modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
            Box(Modifier.rotate(90f)) { DealerIcon(R.drawable.ic_chevron, size = 20.dp) }
        }
    }
    if (open) ModalBottomSheet(onDismissRequest = { open = false }, containerColor = Color.White) {
        Text(label, style = AgentVisitDesign.sheetTitle, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(12.dp))
        if (options.size > 8) Box(Modifier.padding(horizontal = 16.dp)) { SearchField(query, { query = it }, "Поиск", 40.dp) }
        LazyColumn(Modifier.fillMaxWidth().heightIn(max = 420.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
            items(options.filter { it.contains(query, ignoreCase = true) }, key = { it }) { option ->
                Row(Modifier.fillMaxWidth().softCardPress { onSelect(option); open = false }.padding(horizontal = 16.dp, vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(option, style = AgentVisitDesign.name, modifier = Modifier.weight(1f))
                    RadioButton(selected = option == value, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = DealerDesign.accent))
                }
            }
        }
    }
}

@Composable
internal fun DealerProfileButton(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(12.dp))
        .background(if (enabled) DealerDesign.accent else DealerDesign.tertiary)
        .softCardPress(enabled = enabled, onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, style = AgentVisitDesign.button, color = Color.White)
    }
}
