package com.example.indiwarenative

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.switchPreference

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
    if (shouldShowDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                shouldShowDialog.value = false
            },
            modifier = Modifier.fillMaxSize().padding(0.dp),
            properties = DialogProperties(), content = {
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

                                shouldShowDialog.value = false
                            })
                        {
                            Text("Speichern")
                        }
                    }
                }
            })
    }
}
@SuppressLint("MutableCollectionMutableState", "CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Settings(name: String, modifier: Modifier = Modifier) {
    var Kurse by remember { mutableStateOf<ArrayList<String>?>(null) }
    val OwnSubjectDialogToggle = remember { mutableStateOf(false) } // 1

    LaunchedEffect(Unit) {
        Kurse = getKurse("https://www.stundenplan24.de/53102849/mobil/mobdaten/Klassen.xml")
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

    ProvidePreferenceLocals {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            switchPreference(
                key = "showTeachers",
                defaultValue = true,
                title = { Text(text = "Lehrer Anzeigen") },
                icon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = null) },
                summary = { Text(text = if (it) "An" else "Aus") }
            )
        }
    }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    IndiwareNativeTheme {
        Settings("Android")
    }
}