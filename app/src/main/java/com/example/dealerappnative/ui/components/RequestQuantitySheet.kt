package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.data.RequestCatalogProduct
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.RequestPickerDesign as Design
import kotlinx.coroutines.launch

/** Figma 12512:284426: compact title, 48-point input with separate unit, confirm. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RequestQuantitySheet(product: RequestCatalogProduct, quantity: Int,
    onDismiss: () -> Unit, keepKeyboardOpen: Boolean = false, onConfirm: (Int) -> Unit) {
    val initial = quantity.takeIf { it > 0 }?.toString().orEmpty()
    var field by rememberSaveable(product.id, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initial, TextRange(0, initial.length)))
    }
    val value = field.text.toIntOrNull()
    val valid = value != null && value in 0..product.stock
    val error = field.text.isNotEmpty() && !valid
    val focus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    ModalBottomSheet(onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White, scrimColor = Color.Black.copy(alpha = .5f),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        dragHandle = {
            Box(Modifier.fillMaxWidth().height(28.dp)) {
                Box(Modifier.align(Alignment.TopCenter).padding(top = 8.dp).size(40.dp, 4.dp)
                    .background(Color(0xFFF1F1F1), RoundedCornerShape(2.dp)))
            }
        }) {
        Column(Modifier.fillMaxWidth().imePadding().padding(horizontal = 16.dp)) {
            Text("Количество", style = Design.sheetTitle, color = DealerDesign.text)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(12.dp))
                .border(2.dp, if (error) Color(0xFFF76659) else DealerDesign.accent, RoundedCornerShape(12.dp)),
                verticalAlignment = Alignment.CenterVertically) {
                BasicTextField(field, { if (it.text.length <= 4 && it.text.all(Char::isDigit)) field = it },
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp).focusRequester(focus)
                        .semantics { contentDescription = "Количество: ${product.name}" },
                    textStyle = Design.body, cursorBrush = SolidColor(DealerDesign.accent),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true,
                    decorationBox = { input -> Box {
                        if (field.text.isEmpty()) Text("Введите", style = Design.body, color = DealerDesign.tertiary)
                        input()
                    } })
                Box(Modifier.width(64.dp).fillMaxHeight().background(DealerDesign.background), contentAlignment = Alignment.Center) {
                    Text("Штука", style = Design.detail, color = DealerDesign.text)
                }
            }
            if (error) Text("В наличии: ${product.stock} шт", style = Design.detail, color = Color(0xFFF76659),
                modifier = Modifier.padding(top = 4.dp))
            Spacer(Modifier.height(16.dp))
            RequestPickerButton("Подтвердить", valid) {
                if (!keepKeyboardOpen) keyboard?.hide()
                scope.launch { sheetState.hide(); onConfirm(value!!) }
            }
            Spacer(Modifier.height(16.dp))
        }
        LaunchedEffect(Unit) { focus.requestFocus(); keyboard?.show() }
    }
}
