package com.example.dealerappnative.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

/** Figma base, with the user's product-name/price emphasis. Local to this picker. */
internal object RequestPickerDesign {
    val body = DealerDesign.type(15, 20, FontWeight.Normal, -.24f)
    val detail = DealerDesign.type(13, 18, FontWeight.Normal, -.078f)
    val productTitle = DealerDesign.type(15, 20, FontWeight.Medium, -.24f)
    val price = DealerDesign.type(16, 22, FontWeight.SemiBold, -.24f).copy(fontFeatureSettings = "tnum")
    val unit = DealerDesign.type(13, 18, FontWeight.Medium, -.078f)
    val quantity = productTitle.copy(fontFeatureSettings = "tnum")
    val badge = DealerDesign.type(11, 13, FontWeight.SemiBold, .06f)
    val button = DealerDesign.type(15, 20, FontWeight.Normal, -.5f)
    val sheetTitle = DealerDesign.type(17, 22, FontWeight.SemiBold)
    val thumbnail = Color(0xFFF1F1F1)
    val addBackground = Color(0xFFF7EEFC)
    val clear = Color(0xFFB0BABF)
}
