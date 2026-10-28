package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.OpsViewModel
import com.example.ui.components.ReportDialog
import com.example.ui.navigation.NavDestination
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FinancialsScreen
import com.example.ui.screens.GovernanceScreen
import com.example.ui.screens.TeamComplianceScreen
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.OpsManagerTheme
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealLight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpsManagerTheme(darkTheme = true) {
                val viewModel: OpsViewModel = viewModel()
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: OpsViewModel) {
    var currentDestination by remember { mutableStateOf(NavDestination.DASHBOARD) }
    val activeReport by viewModel.activeReport.collectAsStateWithLifecycle()

    // Handle system back gesture
    if (currentDestination != NavDestination.DASHBOARD) {
        BackHandler {
            currentDestination = NavDestination.DASHBOARD
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF0B0F17),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF101721),
                contentColor = TealLight,
                tonalElevation = 8.dp
            ) {
                NavDestination.values().forEach { destination ->
                    val isSelected = currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = destination.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TealAccent,
                            selectedTextColor = TealAccent,
                            indicatorColor = TealContainer,
                            unselectedIconColor = SlateLight,
                            unselectedTextColor = SlateMuted
                        ),
                        modifier = Modifier.testTag("nav_${destination.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0B0F17))
        ) {
            when (currentDestination) {
                NavDestination.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigate = { dest -> currentDestination = dest }
                )
                NavDestination.TEAM_COMPLIANCE -> TeamComplianceScreen(
                    viewModel = viewModel
                )
                NavDestination.GOVERNANCE -> GovernanceScreen(
                    viewModel = viewModel
                )
                NavDestination.FINANCIALS -> FinancialsScreen(
                    viewModel = viewModel
                )
            }

            // Global Export Report Dialog Preview
            activeReport?.let { reportState ->
                ReportDialog(
                    report = reportState,
                    onDismiss = { viewModel.dismissReport() }
                )
            }
        }
    }
}
