package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Поиск по имени и ид",
    height: Dp = 36.dp,
    textStyle: TextStyle = DealerDesign.search
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = Modifier.fillMaxWidth().height(height),
        textStyle = textStyle.copy(color = DealerDesign.text),
        cursorBrush = SolidColor(DealerDesign.accent),
        decorationBox = { textField ->
            Row(
                Modifier.fillMaxSize().clip(RoundedCornerShape(DealerDesign.radius))
                    .background(DealerDesign.background).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DealerIcon(R.drawable.ic_search)
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(placeholder, style = textStyle, color = DealerDesign.tertiary)
                    }
                    textField()
                }
            }
        }
    )
}
