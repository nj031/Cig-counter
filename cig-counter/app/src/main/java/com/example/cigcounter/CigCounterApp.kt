package com.example.cigcounter

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cigcounter.data.AppDatabase
import com.example.cigcounter.data.cigarette.CigaretteRepository
import com.example.cigcounter.navigation.Screen
import com.example.cigcounter.ui.components.BottomNavBar
import com.example.cigcounter.ui.components.BottomNavTab
import com.example.cigcounter.ui.screens.HistoryScreen
import com.example.cigcounter.ui.screens.HomeScreen
import com.example.cigcounter.ui.screens.StatsScreen
import com.example.cigcounter.ui.theme.CigCounterTheme
import com.example.cigcounter.viewmodel.HomeViewModel

@Composable
fun CigCounterApp() {
    val context = LocalContext.current
    val repository = remember {
        CigaretteRepository(AppDatabase.getInstance(context).cigaretteDao())
    }
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    CigCounterTheme {
        Scaffold(
            bottomBar = {
                BottomNavBar(
                    selectedTab = when (currentRoute) {
                        Screen.History.route -> BottomNavTab.HISTORY
                        Screen.Stats.route -> BottomNavTab.STATS
                        else -> BottomNavTab.TODAY
                    },
                    onTabSelected = { tab ->
                        val route = when (tab) {
                            BottomNavTab.HISTORY -> Screen.History.route
                            BottomNavTab.TODAY -> Screen.Home.route
                            BottomNavTab.STATS -> Screen.Stats.route
                        }
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    val homeViewModel: HomeViewModel = viewModel(
                        factory = HomeViewModel.Factory(repository)
                    )
                    HomeScreen(viewModel = homeViewModel)
                }
                composable(Screen.History.route) { HistoryScreen() }
                composable(Screen.Stats.route) { StatsScreen() }
            }
        }
    }
}
