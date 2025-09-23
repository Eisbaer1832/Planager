package com.capputinodevelopment.planager.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarViewDay
import androidx.compose.material.icons.outlined.CalendarViewWeek
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun NavBar(currentScreen: Int, onNavigate: (Int) -> Unit) {
    val items = listOf("Tagesplan", "Wochenplan", "Einstellungen")
    val selectedIcons = listOf(Icons.Filled.CalendarViewDay, Icons.Filled.CalendarViewWeek, Icons.Filled.Settings)
    val unselectedIcons = listOf(Icons.Outlined.CalendarViewDay, Icons.Outlined.CalendarViewWeek, Icons.Outlined.Settings)

    NavigationBar(
        tonalElevation = 0.dp,
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (currentScreen == index) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = item,
                    )
                },
                label = { Text(item) },
                selected = currentScreen == index,
                onClick = { onNavigate(index) }
            )
        }
    }
}
