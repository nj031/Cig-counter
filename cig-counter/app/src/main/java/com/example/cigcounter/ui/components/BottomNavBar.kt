package com.example.cigcounter.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class BottomNavTab { HISTORY, TODAY, STATS }

@Composable
fun BottomNavBar(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == BottomNavTab.HISTORY,
            onClick = { onTabSelected(BottomNavTab.HISTORY) },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "History") },
            label = { Text("History") }
        )
        NavigationBarItem(
            selected = selectedTab == BottomNavTab.TODAY,
            onClick = { onTabSelected(BottomNavTab.TODAY) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Today") },
            label = { Text("Today") }
        )
        NavigationBarItem(
            selected = selectedTab == BottomNavTab.STATS,
            onClick = { onTabSelected(BottomNavTab.STATS) },
            icon = { Icon(Icons.Default.Info, contentDescription = "Stats") },
            label = { Text("Stats") }
        )
    }
}
