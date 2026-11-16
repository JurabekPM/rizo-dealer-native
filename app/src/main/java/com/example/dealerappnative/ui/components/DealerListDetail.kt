package com.example.dealerappnative.ui.components

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.navigation.LocalDealerWindowLayout
import com.example.dealerappnative.ui.theme.DealerDesign

/** A selected document uses the same detail content when the window changes size. */
@Composable
internal fun DealerListDetail(
    hasSelection: Boolean,
    @DrawableRes emptyIcon: Int,
    emptyText: String,
    list: @Composable () -> Unit,
    detail: @Composable () -> Unit
) {
    val split = LocalDealerWindowLayout.current.hasPanes
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val listWidth = (maxWidth * .38f).coerceIn(300.dp, 400.dp)
        Row(Modifier.fillMaxSize()) {
            if (split || !hasSelection) {
                Box(Modifier.then(if (split) Modifier.width(listWidth).clipToBounds() else Modifier.weight(1f))
                    .fillMaxHeight()) {
                    list()
                }
            }
            if (split) VerticalDivider(color = DealerDesign.divider)
            if (split || hasSelection) {
                Box(
                    Modifier.weight(1f).fillMaxHeight().background(DealerDesign.background)
                        .then(if (split) Modifier.clipToBounds() else Modifier),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (hasSelection) {
                        Box(Modifier.widthIn(max = 720.dp).fillMaxSize()) { detail() }
                    } else {
                        Column(
                            Modifier.align(Alignment.Center).padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DealerIcon(emptyIcon, DealerDesign.secondary, 32.dp)
                            Text(emptyText, style = DealerDesign.detail, color = DealerDesign.secondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun Modifier.dealerSelectedCard(isSelected: Boolean): Modifier {
    if (!isSelected) return this
    return then(
        Modifier.border(1.dp, DealerDesign.accent, RoundedCornerShape(DealerDesign.cardRadius))
            .semantics { selected = true }
    )
}

/** Read-only details for Admin documents backed by the existing summary data. */
@Composable
internal fun DealerDocumentDetails(
    documentId: String,
    title: String,
    fields: List<Pair<String, String>>,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val scroll = key(documentId) { rememberScrollState() }
    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        ChildScreenHeader(title, onBack)
        Column(
            Modifier.fillMaxWidth().verticalScroll(scroll).padding(DealerDesign.pageInset),
            verticalArrangement = Arrangement.spacedBy(DealerDesign.sectionGap)
        ) {
            fields.forEach { (label, value) ->
                Column(
                    Modifier.fillMaxWidth().cardShadow().background(DealerDesign.surface)
                        .padding(DealerDesign.pageInset),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(label, style = DealerDesign.detail, color = DealerDesign.secondary)
                    Text(value, style = DealerDesign.body, color = DealerDesign.text)
                }
            }
        }
    }
}
