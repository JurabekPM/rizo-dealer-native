package com.example.dealerappnative

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import com.example.dealerappnative.ui.components.StoreActionsHost
import com.example.dealerappnative.data.VisitReturnsProvider
import com.example.dealerappnative.model.AppRole
import com.example.dealerappnative.screens.AgentScreen
import com.example.dealerappnative.screens.RoleSelectionScreen
import com.example.dealerappnative.ui.theme.DealerPrototypeTheme

/** A fresh launch asks for a role; configuration changes keep the current choice. */
@Composable
internal fun DealerEntry(initialAdminTab: Int = 0) {
    var role by rememberSaveable { mutableStateOf<AppRole?>(null) }
    val screenState = rememberSaveableStateHolder()
    val chooseRole: () -> Unit = { role = null }

    // Administrator child screens handle Back first, then the role chooser is shown.
    BackHandler(enabled = role != null, onBack = chooseRole)
    VisitReturnsProvider {
    when (role) {
        AppRole.Administrator -> screenState.SaveableStateProvider("administrator") {
            StoreActionsHost { DealerApp(initialAdminTab) }
        }
        AppRole.Agent -> DealerPrototypeTheme { StoreActionsHost { AgentScreen(onBack = chooseRole) } }
        null -> DealerPrototypeTheme { RoleSelectionScreen(onSelect = { role = it }) }
    }
    }
}
