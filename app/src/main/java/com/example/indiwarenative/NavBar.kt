package com.example.indiwarenative

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CalendarViewWeek
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun NavBar() {
    val context = LocalContext.current
    val items = listOf("Tagesplan", "Wochenplan", "Einstellungen")
    val selectedIcons = listOf(Icons.Filled.CalendarToday, Icons.Filled.CalendarViewWeek, Icons.Filled.Settings)
    val href = listOf(MainActivity::class.java, MainActivity::class.java, Settings::class.java)
    val unselectedIcons =
        listOf(Icons.Outlined.CalendarToday, Icons.Outlined.CalendarViewWeek, Icons.Outlined.Settings)

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if ( DataSharer.NavbarSelectedItem == index) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = item,
                    )
                },
                label = { Text(item) },
                selected =  DataSharer.NavbarSelectedItem == index,
                onClick = {
                    DataSharer.NavbarSelectedItem = index
                    context.startActivity(Intent(context, href[index]))
                },
            )
        }
    }
}