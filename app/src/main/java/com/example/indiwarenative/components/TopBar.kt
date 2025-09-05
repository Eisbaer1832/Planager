package com.example.indiwarenative.components


import android.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.indiwarenative.data.DataSharer.FilterClass
import com.example.indiwarenative.data.DataSharer.FilterFriend
import com.example.indiwarenative.data.DataSharer.doFilter
import com.example.indiwarenative.data.UserSettings
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopBar(title: String, showHamburger: Boolean) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
            )},

        actions = {
            if (showHamburger) Hamburger()
        },
    )
}

@Composable
fun Hamburger() {
    val context = LocalContext.current
    val userSettings = UserSettings.getInstance(context.applicationContext)
    val friends by userSettings.friendsSubjects.collectAsState(initial = HashMap())
    val friendsClasses by userSettings.friendsClass.collectAsState(initial = HashMap())

    val ownClass by userSettings.ownClass.collectAsState(initial = "")
    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        var expanded by remember { mutableStateOf(false) }
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                leadingIcon = { Icon(Icons.Filled.Groups, contentDescription = "global info") },
                text = { Text("Gesamter Plan") },
                onClick = {
                    doFilter = false
                    FilterClass = ownClass
                }

            )
            DropdownMenuItem(
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "global info") },

                text = { Text("Persönlicher Plan") },
                onClick = {
                    FilterFriend = ""
                    doFilter = true
                    FilterClass = ownClass
                }
            )
            HorizontalDivider()
            friends.forEach { friend ->
                DropdownMenuItem(
                    text = { Text(friend.key) },
                    onClick ={
                        doFilter = true
                        FilterFriend =  friend.key
                        FilterClass = friendsClasses.get(friend.key) ?: ownClass

                    }
                )
            }

        }
    }
}