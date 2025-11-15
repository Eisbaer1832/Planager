package com.capputinodevelopment.planager.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capputinodevelopment.planager.data.DataSharer.FilterClass
import com.capputinodevelopment.planager.data.DataSharer.FilterFriend
import com.capputinodevelopment.planager.data.DataSharer.doFilter
import com.capputinodevelopment.planager.data.UserSettings


@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
fun MoreOptionsSheet (
    showBottomSheet: MutableState<Boolean>,
    userSettings: UserSettings,
    selected: Int,
    friends: HashMap<String, HashMap<String, Boolean>>,

    ) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val ownClass by userSettings.ownClass.collectAsState(initial = "")
    val friendsClasses by userSettings.friendsClass.collectAsState(initial = HashMap())

    if (showBottomSheet.value) {
        var selectedIndex by remember { mutableIntStateOf(selected) }

        println(selectedIndex)
        LaunchedEffect(selectedIndex) {
            when (selectedIndex) {
                0 -> {
                    FilterFriend = ""
                    doFilter = true
                    FilterClass = ownClass
                }
                1 -> {
                    FilterFriend = ""
                    doFilter = false
                    FilterClass = ownClass
                }
                else -> {
                    val key = friends.keys.elementAt(selectedIndex - 2)
                    doFilter = true
                    FilterFriend =  key
                    FilterClass = friendsClasses[key] ?: ownClass
                }
            }
        }
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState
        ) {

            Row(
                Modifier.padding(horizontal = 8.dp)
            ) {
                val size = ButtonDefaults.LargeContainerHeight

                val options = listOf("Eigene Fächer", "Gesamter Plan")
                val unCheckedIcons = listOf(Icons.Outlined.Person, Icons.Outlined.Group)
                val checkedIcons = listOf(Icons.Filled.Person, Icons.Filled.Group)
                val modifiers = listOf(Modifier.weight(1.25f), Modifier.weight(1f))

                Row(
                    Modifier.padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                ) {
                    options.forEachIndexed { index, label ->
                        ToggleButton(
                            checked = selectedIndex == index,
                            onCheckedChange = { selectedIndex = index },
                            modifier = Modifier.then(modifiers[index]).height(size).semantics { role = Role.RadioButton },
                            shapes =
                                when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                },
                        ) {
                            Icon(
                                if (selectedIndex == index) checkedIcons[index] else unCheckedIcons[index],
                                contentDescription = "Localized description",
                            )
                            Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                            Text(label)
                        }
                    }
                }
            }


            HorizontalDivider(modifier = Modifier.padding(8.dp))

            LazyRow(
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalArrangement = if (friends.size < 4) {
                    Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                } else {
                    Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                },
            ) {
                val size = ButtonDefaults.MediumContainerHeight

                items(friends.size) { index ->
                    val entry = friends.entries.elementAt(index)
                    val friendButtonIndex = index + 2  // Offset by 2 (after "Eigene Fächer" and "Gesamter Plan")

                    ToggleButton(
                        checked = selectedIndex == friendButtonIndex,
                        onCheckedChange = { selectedIndex = friendButtonIndex },
                        contentPadding = ButtonDefaults.contentPaddingFor(size),
                        modifier = Modifier
                            .then(
                                if (friends.size < 4) Modifier.fillParentMaxWidth(1f / friends.size)
                                else Modifier
                            )
                            .semantics { role = Role.RadioButton },
                        shapes =
                            when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                friends.entries.size - 1 -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            },
                    ) {
                        Text(entry.key)
                    }
                }
            }
        }
    }
}