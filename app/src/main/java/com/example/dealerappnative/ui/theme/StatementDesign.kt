package com.example.dealerappnative.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.dealerappnative.model.StatementKind

/** Local to the statement flow; other Admin/Agent typography stays unchanged. */
internal object StatementDesign {
    val text = Color(0xFF232B32)
    val secondary = Color(0xFF697781)
    val surface = Color(0xFFF5F5F5)
    val border = Color(0xFFE9EDF0)
    val selected = Color(0xFFF8F2FD)
    val order = Color(0xFFB91C1C)
    val payment = Color(0xFF15803D)
    val returned = Color(0xFF1D4ED8)
    fun operationColor(kind: StatementKind): Color = when (kind) {
        StatementKind.Order -> order
        StatementKind.Payment -> payment
        StatementKind.Return -> returned
        StatementKind.Request -> text
    }
    val title = DealerDesign.type(20, 26, FontWeight.SemiBold, -.25f)
    val name = DealerDesign.type(14, 20, FontWeight.SemiBold, -.15f)
    val amount = DealerDesign.type(15, 20, FontWeight.SemiBold, -.2f)
    val body = DealerDesign.type(13, 18, FontWeight.Medium, -.05f)
    val label = DealerDesign.type(12, 16, FontWeight.SemiBold, 0f)
    val caption = DealerDesign.type(12, 16, FontWeight.Medium, 0f)
    val choice = DealerDesign.type(13, 18, FontWeight.SemiBold, -.08f)
    val button = DealerDesign.type(15, 20, FontWeight.SemiBold, -.15f)
}
