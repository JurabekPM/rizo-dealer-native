package com.example.dealerappnative.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

/** Figma SearchSelection 13744:262806. Kept local to avoid restyling other screens. */
internal object RequestPickerDesign {
    val body = DealerDesign.type(15, 20, FontWeight.Normal, -.24f)
    val detail = DealerDesign.type(13, 18, FontWeight.Normal, -.078f)
    val badge = DealerDesign.type(11, 13, FontWeight.SemiBold, .06f)
    val button = DealerDesign.type(15, 20, FontWeight.Normal, -.5f)
    val sheetTitle = DealerDesign.type(17, 22, FontWeight.SemiBold)
    val thumbnail = Color(0xFFF1F1F1)
    val available = Color(0xFF22C348)
    val addBackground = Color(0xFFF4E6FC)
    val clear = Color(0xFFB0BABF)
    val dot = Color(0xFFD5DADD)
}
