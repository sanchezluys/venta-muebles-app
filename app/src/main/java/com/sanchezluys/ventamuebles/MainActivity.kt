package com.sanchezluys.ventamuebles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanchezluys.ventamuebles.ui.screens.DashboardScreen
import com.sanchezluys.ventamuebles.ui.screens.ReportsScreen
import com.sanchezluys.ventamuebles.ui.screens.SaleFormScreen
import com.sanchezluys.ventamuebles.ui.screens.SettingsScreen
import com.sanchezluys.ventamuebles.ui.theme.VentaMueblesTheme
import com.sanchezluys.ventamuebles.ui.viewmodel.DashboardViewModel
import com.sanchezluys.ventamuebles.ui.viewmodel.ReportsViewModel
import com.sanchezluys.ventamuebles.ui.viewmodel.SaleFormViewModel
import com.sanchezluys.ventamuebles.ui.viewmodel.SettingsViewModel

enum class Screen(
    val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    DASHBOARD(
        R.string.nav_dashboard,
        Icons.Filled.Home,
        Icons.Outlined.Home,
        "nav_item_dashboard"
    ),
    NEW_SALE(
        R.string.nav_new_sale,
        Icons.Filled.AddShoppingCart,
        Icons.Outlined.AddShoppingCart,
        "nav_item_new_sale"
    ),
    REPORTS(
        R.string.nav_reports,
        Icons.Filled.Assessment,
        Icons.Outlined.Assessment,
        "nav_item_reports"
    ),
    SETTINGS(
        R.string.nav_settings,
        Icons.Filled.Settings,
        Icons.Outlined.Settings,
        "nav_item_settings"
    )
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as VentaMueblesApp
        val repository = app.repository
        val settings = app.settings

        val dashboardViewModel by viewModels<DashboardViewModel> {
            DashboardViewModel.Factory(repository, settings)
        }
        val saleFormViewModel by viewModels<SaleFormViewModel> {
            SaleFormViewModel.Factory(repository)
        }
        val reportsViewModel by viewModels<ReportsViewModel> {
            ReportsViewModel.Factory(repository, settings)
        }
        val settingsViewModel by viewModels<SettingsViewModel> {
            SettingsViewModel.Factory(settings)
        }

        setContent {
            VentaMueblesTheme {
                MainApp(
                    dashboardViewModel = dashboardViewModel,
                    saleFormViewModel = saleFormViewModel,
                    reportsViewModel = reportsViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}

@Composable
fun MainApp(
    dashboardViewModel: DashboardViewModel,
    saleFormViewModel: SaleFormViewModel,
    reportsViewModel: ReportsViewModel,
    settingsViewModel: SettingsViewModel
) {
    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    Screen.values().forEach { screen ->
                        val selected = currentScreen == screen
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentScreen = screen },
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = stringResource(screen.titleRes),
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(screen.titleRes),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag(screen.tag)
                        )
                    }
                }
            }
        ) { innerPadding ->
            when (currentScreen) {
                Screen.DASHBOARD -> DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToNewSale = { currentScreen = Screen.NEW_SALE },
                    onNavigateToReports = { currentScreen = Screen.REPORTS },
                    modifier = Modifier.padding(innerPadding)
                )

                Screen.NEW_SALE -> SaleFormScreen(
                    viewModel = saleFormViewModel,
                    onNavigateBack = { currentScreen = Screen.DASHBOARD },
                    modifier = Modifier.padding(innerPadding)
                )

                Screen.REPORTS -> ReportsScreen(
                    viewModel = reportsViewModel,
                    onNavigateBack = { currentScreen = Screen.DASHBOARD },
                    modifier = Modifier.padding(innerPadding)
                )

                Screen.SETTINGS -> SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { currentScreen = Screen.DASHBOARD },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
