package com.capputinodevelopment.planager.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarViewDay
import androidx.compose.material.icons.outlined.CalendarViewWeek
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NavBar(currentScreen: Int, onNavigate: (Int) -> Unit, padding: PaddingValues) {
    val items = listOf("Tagesplan", "Wochenplan", "Recherche", "Einstellungen")
    val selectedIcons = listOf(Icons.Filled.CalendarViewDay, Icons.Filled.CalendarViewWeek, Icons.Filled.Search, Icons.Filled.Settings)
    val unselectedIcons = listOf(Icons.Outlined.CalendarViewDay, Icons.Outlined.CalendarViewWeek, Icons.Outlined.Search, Icons.Outlined.Settings)

    NavigationRail(
        modifier = Modifier.padding(padding)
    ){
        items.forEachIndexed { index, item ->
            NavigationRailItem(
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
