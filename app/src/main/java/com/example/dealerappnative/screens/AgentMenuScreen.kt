package com.example.dealerappnative.screens

import com.example.dealerappnative.ui.components.softCardPress
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat
import com.example.dealerappnative.R
import com.example.dealerappnative.data.AgentMenuSampleData
import com.example.dealerappnative.ui.components.AgentMenuRow
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.theme.AgentDesign

@Composable
internal fun AgentMenuScreen(onExit: () -> Unit, onStoresClick: () -> Unit) {
    val data = AgentMenuSampleData
    val context = LocalContext.current
    val version = remember(context) {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        "version: ${info.versionName} (${PackageInfoCompat.getLongVersionCode(info)})"
    }
    Column(Modifier.fillMaxSize().background(AgentDesign.background)) {
        LazyColumn(Modifier.fillMaxWidth().weight(1f)) {
            item(key = "profile") { AgentProfile() }
            item(key = "main-menu") {
                Spacer(Modifier.height(AgentDesign.gap))
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(AgentDesign.radius)).background(Color.White)) {
                    AgentMenuRow("Синхронизация", R.drawable.ic_agent_refresh, subtitle = data.lastSync)
                    AgentMenuRow("Расписание", R.drawable.ic_agent_schedule)
                    AgentMenuRow("Магазины", R.drawable.ic_store, onClick = onStoresClick)
                    AgentMenuRow("Настройки", R.drawable.ic_settings)
                    AgentMenuRow("Уведомления", R.drawable.ic_bell, badge = data.notifications, divider = false)
                }
            }
            item(key = "account-menu") {
                Spacer(Modifier.height(AgentDesign.gap))
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(AgentDesign.radius)).background(Color.White)) {
                    AgentMenuRow("О Приложении", R.drawable.ic_info)
                    AgentMenuRow("Выйти", R.drawable.ic_exit, divider = false, onClick = onExit)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
        Text(version, style = AgentDesign.menuDetail, color = AgentDesign.secondary,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 12.dp))
    }
}

@Composable
private fun AgentProfile() {
    val data = AgentMenuSampleData
    Row(Modifier.fillMaxWidth().background(Color.White).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(48.dp).clip(CircleShape).background(AgentDesign.tint), contentAlignment = Alignment.Center) {
            Text(data.initials, style = AgentDesign.date, color = AgentDesign.accent)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(data.name, style = AgentDesign.date, color = AgentDesign.text,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(data.phone, style = AgentDesign.menuDetail, color = AgentDesign.secondary)
        }
        Spacer(Modifier.width(12.dp))
        DealerIcon(R.drawable.ic_edit, AgentDesign.secondary, 24.dp)
    }
}
