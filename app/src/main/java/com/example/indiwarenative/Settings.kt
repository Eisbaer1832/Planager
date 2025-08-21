package com.example.indiwarenative

import android.annotation.SuppressLint
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment

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
fun OwnSubjectDialog(
    shouldShowDialog: MutableState<Boolean>,
    Kurse: ArrayList<Kurs>?,
    userSettings: UserSettings
) {
    if (shouldShowDialog.value) {
        val couroutineScope = rememberCoroutineScope()
        val status by userSettings.ownSubjects.collectAsState(initial = HashMap<String, Boolean>())

        BasicAlertDialog(
            onDismissRequest = {
                shouldShowDialog.value = false
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
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
                        Kurse?.size?.let {
                            for (i in 0..<it) {
                                Row {
                                    Card(
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Row {
                                            Text(
                                                modifier = Modifier.padding(10.dp),
                                                text = Kurse[i].subject + " " + Kurse[i].teacher
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            var checked by remember { mutableStateOf(status.get(Kurse[i].subject) == true) }
                                            Switch(
                                                checked = checked,
                                                onCheckedChange = {
                                                    checked = it
                                                    status.put(Kurse[i].subject, checked)
                                                    couroutineScope.launch{userSettings.updateOwnSubjects(status)}
                                                })
                                        }
                                    }
                                }
                            }
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                couroutineScope.launch{userSettings.updateOwnSubjects(status)}
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

    val context = LocalContext.current
    val userSettings = UserSettings.getInstance(context.applicationContext)
    val showTeacher by userSettings.showTeacher.collectAsState(initial = false)

    var Kurse by remember { mutableStateOf<ArrayList<Kurs>?>(null) }
    val OwnSubjectDialogToggle = remember { mutableStateOf(false) } // 1
    val couroutineScope = rememberCoroutineScope()
    println(showTeacher)

    LaunchedEffect(Unit) {
        Kurse = getKurse("https://www.stundenplan24.de/53102849/mobil/mobdaten/Klassen.xml")
        println("Kurse: " + (Kurse as Iterable<Any?>).joinToString())
    }

    if (OwnSubjectDialogToggle.value) {
        OwnSubjectDialog(shouldShowDialog = OwnSubjectDialogToggle, Kurse,userSettings)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Eigene Fächer", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = {OwnSubjectDialogToggle.value = true}) { Text("Ändern") }
            }

        }
        var checked by remember { mutableStateOf(true) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Lehrer anzeigen")
            Spacer(modifier = Modifier.weight(1f)) // pushes the Switch to the end
            Switch(
                checked = showTeacher,
                onCheckedChange = {
                    checked = it
                    couroutineScope.launch{userSettings.updateShowTeachers(checked)}
                }
            )
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