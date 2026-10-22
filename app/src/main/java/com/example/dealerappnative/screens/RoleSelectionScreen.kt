package com.example.dealerappnative.screens

import com.example.dealerappnative.ui.components.softCardPress
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.AppRole
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.DealerPrototypeTheme

@Composable
internal fun RoleSelectionScreen(onSelect: (AppRole) -> Unit) {
    Column(Modifier.fillMaxSize().background(DealerDesign.background).safeDrawingPadding()
        .verticalScroll(rememberScrollState()).padding(horizontal = DealerDesign.pageInset, vertical = 24.dp),
        verticalArrangement = Arrangement.Center) {
        Text("Dealer", style = DealerDesign.title, color = DealerDesign.accent)
        Spacer(Modifier.height(24.dp))
        Text("Войти как", style = DealerDesign.title)
        Spacer(Modifier.height(8.dp))
        Text("Выберите роль", style = DealerDesign.detail, color = DealerDesign.secondary)
        Spacer(Modifier.height(24.dp))
        RoleCard("Администратор", "Управление дилером", R.drawable.ic_people) {
            onSelect(AppRole.Administrator)
        }
        Spacer(Modifier.height(DealerDesign.sectionGap))
        RoleCard("Агент", "Работа агента", R.drawable.ic_person_outline) {
            onSelect(AppRole.Agent)
        }
    }
}

@Composable
private fun RoleCard(title: String, subtitle: String, @DrawableRes icon: Int, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().softCardPress(onClick = onClick).clip(RoundedCornerShape(DealerDesign.radius))
        .background(Color.White).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(48.dp).clip(RoundedCornerShape(DealerDesign.radius))
            .background(DealerDesign.background), contentAlignment = Alignment.Center) {
            DealerIcon(icon, DealerDesign.accent)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = DealerDesign.cardTitle, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, style = DealerDesign.detail, color = DealerDesign.secondary)
        }
        Spacer(Modifier.width(8.dp))
        DealerIcon(R.drawable.ic_chevron)
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun RoleSelectionPreview() = DealerPrototypeTheme { RoleSelectionScreen {} }
