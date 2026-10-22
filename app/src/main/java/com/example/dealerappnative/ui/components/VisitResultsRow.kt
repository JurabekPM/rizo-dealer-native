package com.example.dealerappnative.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.dealerappnative.R
import com.example.dealerappnative.model.VisitResult
import com.example.dealerappnative.ui.theme.DealerDesign

/** Overlapping 28 dp badges: 7 dp overlap is 25%, with original 20 dp result icons. */
@Composable
internal fun VisitResultsRow(results: List<VisitResult>, storeId: String? = null) {
    Row(Modifier.height(28.dp).semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy((-7).dp), verticalAlignment = Alignment.CenterVertically) {
        results.forEachIndexed { index, result ->
            VisitResultBadge(result, storeId, index)
        }
    }
}

/** The same badge is shared from its compact stack into the corresponding detail row. */
@Composable
internal fun VisitResultBadge(result: VisitResult, storeId: String?, layer: Int = result.ordinal, inDetails: Boolean = false) {
    val icon = resultIcon(result)
    val badgeSize = if (inDetails) 32.dp else 28.dp
    val iconSize = if (inDetails) 24.dp else 20.dp
    Box(Modifier.size(badgeSize).zIndex(layer.toFloat()).sharedVisitResult(storeId, result, layer)
        .background(DealerDesign.background, CircleShape).border(1.5.dp, Color.White, CircleShape)
        .semantics { contentDescription = icon.label }, contentAlignment = Alignment.Center) {
        DealerIcon(icon.resource, if (result == VisitResult.Collection) Color.Unspecified else DealerDesign.secondary,
            size = iconSize)
    }
}

private data class ResultIcon(val label: String, @param:DrawableRes @get:DrawableRes val resource: Int)

private val resultIcons = mapOf(
    VisitResult.Delivery to ResultIcon("Доставка выполнена", R.drawable.ic_box),
    VisitResult.Request to ResultIcon("Заявка создана", R.drawable.ic_visit),
    VisitResult.Revision to ResultIcon("Ревизия выполнена", R.drawable.ic_edit),
    VisitResult.Collection to ResultIcon("Инкассация выполнена", R.drawable.img_visit_collection_result),
    VisitResult.Return to ResultIcon("Возврат выполнен", R.drawable.ic_return)
)

private fun resultIcon(result: VisitResult): ResultIcon = resultIcons.getValue(result)
