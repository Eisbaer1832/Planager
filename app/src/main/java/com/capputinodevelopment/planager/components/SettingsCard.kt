package com.capputinodevelopment.planager.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.backend.fetchTimetable
import com.capputinodevelopment.planager.data.backend.getAllClasses
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SettingsCardEdit(
    title: String,
    shape: RoundedCornerShape,
    buttonIcon: ImageVector = Icons.Default.Edit,
    buttonText: String = "Ändern",
    size: Dp = 16.dp,
    onclick: () -> Unit,
    leadingIcon: Int? = null,

    ) {
    Card(
    shape = shape,
    modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(size),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Image(
                    modifier = Modifier.size(40.dp).padding(end = 10.dp),
                    painter = painterResource(id = leadingIcon),
                    contentDescription = "KoFi Logo"
                )
            }
            Text(title)
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {onclick()},
            ) {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = buttonIcon,
                        contentDescription = "Favorite",
                        modifier = Modifier
                            .size(30.dp)
                    )
                    Text(buttonText)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCardDropdown(
    title: String,
    shape: RoundedCornerShape,
    dropDownList: Array<String>,
    size: Dp = 16.dp,
    onclick: (String) -> Unit,
    default: String
) {
    Card(
        shape = shape,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(size),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var expanded by remember { mutableStateOf(false) }
            var selectedOptionText by remember { mutableStateOf(default) }
            selectedOptionText = default

            Text(title)
            Spacer(modifier = Modifier.weight(1f))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.width(170.dp)
                ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor(),
                    readOnly = true,
                    value = selectedOptionText,
                    onValueChange = {},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    dropDownList.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                selectedOptionText = selectionOption
                                expanded = false
                                onclick(selectionOption)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun SettingsCardInput(
    shape: RoundedCornerShape,
    userSettings: UserSettings,
    title: String,
    icon: ImageVector,
    default: String,
    initEffect: suspend CoroutineScope.(UserSettings) -> String = { settings ->
        settings.password.first() // async load
    },
    updateEffect: suspend CoroutineScope.(String, UserSettings) -> Unit = { value, settings ->
        settings.updatePassword(value) // async save
    },
    hide: Boolean = false
) {
    Card(
        shape = shape,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var inVal by remember { mutableStateOf(default) }

            TextField(
                visualTransformation = if(hide)  PasswordVisualTransformation() else VisualTransformation.None,
                value = inVal,
                onValueChange = { inVal = it },
                label = { Text(title) },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Password"
                    )
                },
                singleLine = true,
            )

            // run initEffect ONCE, override default if needed
            LaunchedEffect(userSettings) {
                val initial = initEffect(this, userSettings)
                if (initial.isNotEmpty()) {
                    inVal = initial
                }
            }

            // update when user types
            LaunchedEffect(inVal) {
                snapshotFlow { inVal }
                    .debounce(500)
                    .collect { value ->
                        updateEffect(this, value, userSettings)
                    }
            }
        }
    }
}

@Composable
fun CheckCredentials(snackbarHostState: SnackbarHostState, onValidationChanged: (state: Boolean) -> Unit, context: Context) {
    val couroutineScope = rememberCoroutineScope()
    val userSettings = UserSettings.getInstance(context.applicationContext)


    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            couroutineScope.launch {
                val result = fetchTimetable(
                    userSettings = userSettings,
                    url = "/mobil/mobdaten/Klassen.xml",
                    lContext = context
                )

                if (result.isEmpty()) {
                    println("update failed")
                    couroutineScope.launch {
                        snackbarHostState.showSnackbar("Nutzerdaten inkorrekt!")
                    }
                    onValidationChanged(false)
                } else {
                    couroutineScope.launch {
                        snackbarHostState.showSnackbar("Nutzerdaten korrekt!!")
                    }
                    getAllClasses(userSettings, "/mobil/mobdaten/Klassen.xml", context)?: arrayOf(String())
                    onValidationChanged(true)
                }
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ), shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = "Verbindung testen"
        )
    }
}