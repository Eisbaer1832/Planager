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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.example.indiwarenative.components.FriendCreateDialog
import com.example.indiwarenative.components.FriendItem
import com.example.indiwarenative.components.NavBar
import com.example.indiwarenative.components.SubjectDialog
import com.example.indiwarenative.components.TopBar

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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FriendsList (
    showBottomSheet: MutableState<Boolean>,
    Kurse: ArrayList<Kurs>?,
    userSettings: UserSettings
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val friends by userSettings.friendsSubjects.collectAsState(initial = HashMap())
    val shouldShowDialog = remember { mutableStateOf(false) }
    val createFriendDialog = remember { mutableStateOf(false) }
    val couroutineScope = rememberCoroutineScope()

    if (shouldShowDialog.value) {
        SubjectDialog(shouldShowDialog, Kurse, userSettings, false)
    }

    if (createFriendDialog.value) {
        FriendCreateDialog({ createFriendDialog.value = false }, {name: String ->
            friends.put(name, HashMap())
            createFriendDialog.value = false;
        }, "Freund Erfinden")
    }

    if (showBottomSheet.value) {

        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState
        ) {
            Column {
                friends.forEach {friend ->
                    FriendItem(friend.key, {})
                }
            }
            Button(
                modifier = Modifier
                    .padding(16.dp, 0.dp)
                    .fillMaxWidth(),
                onClick = {
                createFriendDialog.value = true
            }) {
                Text("Freund hinzufügen")
            }
            Button(
                modifier = Modifier
                    .padding( 16.dp, 0.dp)
                    .fillMaxWidth(),
                onClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet.value = false
                    }
                }
            }) {
                Text("Fertig")
            }
        }
    }
}



@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("MutableCollectionMutableState", "CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Settings(name: String, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val userSettings = UserSettings.getInstance(context.applicationContext)
    val showTeacher by userSettings.showTeacher.collectAsState(initial = false)

    var Kurse by remember { mutableStateOf<ArrayList<Kurs>?>(null) }
    val FriendsListToggle = remember { mutableStateOf(false) }
    val OwnSubjectDialogToggle = remember { mutableStateOf(false) }
    val couroutineScope = rememberCoroutineScope()
    println(showTeacher)

    LaunchedEffect(Unit) {
        Kurse = getKurse("https://www.stundenplan24.de/53102849/mobil/mobdaten/Klassen.xml")
        println("Kurse: " + (Kurse as Iterable<Any?>).joinToString())
    }

    if (OwnSubjectDialogToggle.value) {
        SubjectDialog(shouldShowDialog = OwnSubjectDialogToggle, Kurse, userSettings, true)
    }
    if (FriendsListToggle.value) {
        FriendsList(FriendsListToggle, Kurse,userSettings)
    }



    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("App Einstellungen", style = MaterialTheme.typography.headlineMediumEmphasized)

        Card(
            shape = RoundedCornerShape(16.dp, 16.dp, 0.dp,0.dp),
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

        Card(
            shape = RoundedCornerShape(0.dp,0.dp, 16.dp, 16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Fächer von Freunden", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = {FriendsListToggle.value = true}) { Text("Ändern") }
            }

        }
        var checked by remember { mutableStateOf(true) }

        Card(
            shape = RoundedCornerShape(16.dp,16.dp, 16.dp, 16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Lehrer anzeigen")
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = showTeacher,
                    onCheckedChange = {
                        checked = it
                        couroutineScope.launch{userSettings.updateShowTeachers(checked)}
                    }
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("Server Daten", style = MaterialTheme.typography.headlineMediumEmphasized)
        Card(
            shape = RoundedCornerShape(16.dp,16.dp, 0.dp, 0.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var url by remember { mutableStateOf("") }

                    TextField(
                        value = url,
                        onValueChange = {
                            url = it;
                            couroutineScope.launch { userSettings.updateSchoolID(url) }
                        },
                        label = { Text("URL") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Web,
                                contentDescription = "URL"
                            )
                        },
                        singleLine = true,
                    )
                }
        }
        var username by remember { mutableStateOf("") }

        Card(
            shape = RoundedCornerShape(0.dp,0.dp, 0.dp, 0.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextField(
                    value = username,
                    onValueChange = {
                        username = it
                        couroutineScope.launch { userSettings.updateUsername(username) }
                    },
                    label = { Text("Nutzername") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Nutzername"
                        )
                    },
                    singleLine = true,
                )
            }
        }

        var password by remember { mutableStateOf("") }
        Card(
            shape = RoundedCornerShape(0.dp,0.dp, 16.dp, 16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var password by remember { mutableStateOf("") }

                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                        couroutineScope.launch { userSettings.updatePassword(password) }
                    },
                    label = { Text("Passwort") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Password,
                            contentDescription = "Password"
                        )
                    },
                    singleLine = true,
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