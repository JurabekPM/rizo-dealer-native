package com.example.dealerappnative.screens

import com.example.dealerappnative.ui.components.softCardPress
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.sampleStores
import com.example.dealerappnative.model.StoreUiModel
import com.example.dealerappnative.model.StoreLocation
import com.example.dealerappnative.ui.components.ChildScreenHeader
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.components.PrototypeCreateButton
import com.example.dealerappnative.ui.components.SearchField
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun StoresScreen(
    locations: Map<String, StoreLocation>, onBack: () -> Unit, onStoreClick: (String) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val search = query.trim()
    val stores = sampleStores.map { store ->
        val point = locations[store.id]
        val changed = point != null && (point.latitude != store.latitude || point.longitude != store.longitude)
        store.copy(address = point?.address ?: store.address,
            distance = point?.route?.distanceText ?: if (changed) "…" else store.distance)
    }.filter {
        it.name.contains(search, ignoreCase = true) || it.address.contains(search, ignoreCase = true)
    }
    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        ChildScreenHeader("Магазины", onBack, showFilter = true)
        Column(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)) {
            SearchField(query, { query = it }, placeholder = "Поиск")
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(stores, key = { it.id }) { store ->
                StoreCard(store, onClick = { onStoreClick(store.id) })
            }
            if (stores.isEmpty()) {
                item {
                    Text("Магазины не найдены", style = DealerDesign.body, color = DealerDesign.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp))
                }
            }
        }
        PrototypeCreateButton(label = "+ Добавить")
    }
}

@Composable
private fun StoreCard(store: StoreUiModel, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().height(DealerDesign.cardHeight)
        .softCardPress(onClick = onClick).clip(RoundedCornerShape(DealerDesign.radius)).background(Color.White)
        .padding(horizontal = DealerDesign.pageInset)) {
        Row(Modifier.fillMaxWidth().height(72.dp), verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_store)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(store.name, style = DealerDesign.cardTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(store.debtDirection.formatAmount(store.debt), style = DealerDesign.detail,
                    color = store.debtDirection.color, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        HorizontalDivider(color = DealerDesign.divider)
        Row(Modifier.fillMaxWidth().weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(store.address, style = DealerDesign.detail, color = DealerDesign.secondary,
                modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.width(12.dp))
            Text(store.distance, style = DealerDesign.detail, color = DealerDesign.secondary,
                maxLines = 1, softWrap = false)
        }
    }
}
