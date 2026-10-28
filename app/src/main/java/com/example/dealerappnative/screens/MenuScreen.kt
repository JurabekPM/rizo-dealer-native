package com.example.dealerappnative.screens

import com.example.dealerappnative.ui.components.softCardPress
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.ui.components.SoftIconButton
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.components.MenuRow
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun MenuScreen(onStoresClick: () -> Unit = {}, onReturnsClick: () -> Unit = {}, onRevisionsClick: () -> Unit = {}, onEditDealerClick: () -> Unit = {}) {
    Column(Modifier.fillMaxSize().background(DealerDesign.background).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)).background(Color.White).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.dealer_avatar), contentDescription = null,
                modifier = Modifier.size(48.dp).clip(CircleShape), contentScale = ContentScale.Crop)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Хусанов Акбар", style = DealerDesign.body)
                Text("+998 90 116 51 15", color = DealerDesign.secondary, style = DealerDesign.detail)
            }
            SoftIconButton(onClick = onEditDealerClick, modifier = Modifier.size(44.dp).semantics { contentDescription = "Изменить данные дилера" }) {
                DealerIcon(R.drawable.ic_edit, DealerDesign.secondary, 24.dp)
            }
        }
        Spacer(Modifier.height(8.dp))
        Column(Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White).padding(vertical = 4.dp)) {
            menuItems.forEachIndexed { index, (label, iconRes) ->
                MenuRow(label, iconRes, divider = index != menuItems.lastIndex,
                    onClick = when (iconRes) {
                        R.drawable.ic_store -> onStoresClick
                        R.drawable.ic_edit -> onRevisionsClick
                        R.drawable.ic_return -> onReturnsClick
                        else -> null
                    })
            }
        }
        Spacer(Modifier.height(8.dp))
        Column(Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White).padding(vertical = 4.dp)) {
            MenuRow("Настройки", R.drawable.ic_settings, divider = false)
        }
        Spacer(Modifier.height(8.dp))
        Column(Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White).padding(vertical = 4.dp)) {
            MenuRow("О приложении", R.drawable.ic_info)
            MenuRow("Выйти", R.drawable.ic_exit, divider = false)
        }
    }
}

private val menuItems = listOf(
    "Информация о дилере" to R.drawable.ic_dealer_info,
    "Регионы доставки" to R.drawable.ic_pin,
    "Скидка" to R.drawable.ic_discount,
    "Акция и нагрузка" to R.drawable.ic_gift,
    "Категория" to R.drawable.ic_category,
    "Ревизии" to R.drawable.ic_edit,
    "Возвраты" to R.drawable.ic_return,
    "Роли и доступы" to R.drawable.ic_people,
    "Магазин" to R.drawable.ic_store,
    "Уведомление" to R.drawable.ic_bell
)
