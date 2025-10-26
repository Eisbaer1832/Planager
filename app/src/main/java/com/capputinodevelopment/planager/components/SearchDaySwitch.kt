package com.capputinodevelopment.planager.components
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import com.capputinodevelopment.planager.data.DataSharer
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchDaySwitch() {
    var expanded by remember { mutableStateOf(false) }
    val focusRequester = remember {  FocusRequester() }


    val items =
        listOf(
            Icons.Default.CalendarToday to "Freitag",
            Icons.Default.CalendarToday to "Donnerstag",
            Icons.Default.CalendarToday to "Mittwoch",
            Icons.Default.CalendarToday to "Dienstag",
            Icons.Default.CalendarToday to "Montag",
        )


    FloatingActionButtonMenu(
        expanded = expanded,
        button = {
            ToggleFloatingActionButton(
                modifier =
                    Modifier.semantics {
                        traversalIndex = -1f
                        stateDescription = if (expanded) "Expanded" else "Collapsed"
                        contentDescription = "Toggle menu"
                    }
                        .animateFloatingActionButton(
                            visible = true,
                            alignment = Alignment.BottomEnd,
                        )
                        .focusRequester(focusRequester),
                checked = expanded,
                onCheckedChange = { expanded = !expanded },
            ) {
                val imageVector by remember {
                    derivedStateOf {
                        if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Today
                    }
                }
                Icon(
                    painter = rememberVectorPainter(imageVector),
                    contentDescription = null,
                    modifier = Modifier.animateIcon({ checkedProgress }),
                )
            }
        },
        content = {
            items.forEachIndexed { i, item ->
                FloatingActionButtonMenuItem(
                    onClick = {
                        expanded = false
                        DataSharer.searchDay.value = DayOfWeek.of(5 - i )

                    },
                    icon = { Icon(item.first, contentDescription = null) },
                    text = { Text(text = item.second) },
                )
            }
        },
    )
}