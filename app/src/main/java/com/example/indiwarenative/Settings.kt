package com.example.indiwarenative

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings.Global.getString
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.indiwarenative.ui.theme.IndiwareNativeTheme
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.edit

class Settings : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IndiwareNativeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopBar("Einstellungen")
                    }, bottomBar = {
                        NavBar()
                    }
                ) { innerPadding ->
                    Settings(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnSubjectDialog(shouldShowDialog: MutableState<Boolean>, Kurse: ArrayList<String>?) {
    val context = LocalContext.current
    val prefs: SharedPreferences =
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    if (shouldShowDialog.value) {
        androidx.compose.material3.AlertDialog(
            modifier = Modifier.fillMaxSize().padding(0.dp),
            onDismissRequest = {
                shouldShowDialog.value = false
            },
            ){
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                ) {
                    Text("Eigene Fächer")
                    Kurse?.forEach { subject ->
                        Row {
                            Card(
                                modifier = Modifier.padding(10.dp).fillMaxWidth()
                            ) {
                                Row {
                                    Text(
                                        modifier = Modifier.padding(10.dp),
                                        text = subject)
                                    Spacer(modifier = Modifier.weight(1f))
                                    var checked by remember { mutableStateOf(true) }
                                    Switch(
                                        checked = checked,
                                        onCheckedChange = {
                                            checked = it })
                                }
                            }
                        }
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            prefs.edit() {
                                putInt("ownSubjects", 1)
                        }
                            shouldShowDialog.value = false
                        })
                    {
                        Text("Speichern")
                    }
                }
            }
        }
    }
}
@SuppressLint("MutableCollectionMutableState")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Settings(name: String, modifier: Modifier = Modifier) {
    var Kurse by remember { mutableStateOf<ArrayList<String>?>(null) }
    val OwnSubjectDialogToggle = remember { mutableStateOf(false) } // 1

    LaunchedEffect(Unit) {
        Kurse = getKurse()
        println(Kurse)

    }

    if (OwnSubjectDialogToggle.value) {
        OwnSubjectDialog(shouldShowDialog = OwnSubjectDialogToggle, Kurse)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Example: Dark Mode Toggle
        var darkMode by remember { mutableStateOf(false) }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Eigene Fächer", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = {OwnSubjectDialogToggle.value = true}) { Text("Ändern") }
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = darkMode,
                    onCheckedChange = { darkMode = it },
                )
            }
        }

        // Example: Notifications
        var notificationsEnabled by remember { mutableStateOf(true) }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Enable Notifications", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it },
                )
            }
        }

        // Example: Button
        FilledTonalButton(
            onClick = { /* perform some action */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Settings")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    IndiwareNativeTheme {
        Settings("Android")
    }
}