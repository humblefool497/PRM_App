package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Group
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD(
        title = "Dashboard",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    ),
    TEAM_COMPLIANCE(
        title = "Team",
        selectedIcon = Icons.Filled.Group,
        unselectedIcon = Icons.Outlined.Group
    ),
    GOVERNANCE(
        title = "Governance",
        selectedIcon = Icons.Filled.Gavel,
        unselectedIcon = Icons.Outlined.Gavel
    ),
    FINANCIALS(
        title = "Financials",
        selectedIcon = Icons.Filled.AccountBalanceWallet,
        unselectedIcon = Icons.Outlined.AccountBalanceWallet
    )
}
