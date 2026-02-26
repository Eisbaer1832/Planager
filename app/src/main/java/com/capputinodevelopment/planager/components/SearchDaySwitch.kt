package com.capputinodevelopment.planager.components
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.unit.dp
import com.capputinodevelopment.planager.data.Globals
import java.time.LocalDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchDaySwitch() {
    var expanded by remember { mutableStateOf(false) }
    val focusRequester = remember {  FocusRequester() }


    if(expanded) {
        DatePickerModal(
            {
                if (it != null) Globals.searchDay.value = it
            },
            {expanded = false}
        )
    }

    ToggleFloatingActionButton(
        containerSize= { 80.dp },
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

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState( initialSelectedDate = Globals.searchDay.value)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.getSelectedDate())
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

