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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.example.indiwarenative.data.backend.getKurse
import com.example.indiwarenative.components.FriendCreateDialog
import com.example.indiwarenative.components.FriendItem
import com.example.indiwarenative.components.NavBar
import com.example.indiwarenative.components.SubjectDialog
import com.example.indiwarenative.components.TopBar
import com.example.indiwarenative.data.Kurs
import com.example.indiwarenative.data.UserSettings
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first

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
                        TopBar("Einstellungen", false)
                    }, bottomBar = {
                        NavBar()
                    }
                ) { innerPadding ->

                    Settings(
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
    var friendName by remember { mutableStateOf("") }
    if (shouldShowDialog.value) {
        println("friend opening with $friendName")
        SubjectDialog(shouldShowDialog, Kurse, userSettings, false, friendName)
    }

    if (createFriendDialog.value) {
        FriendCreateDialog({ createFriendDialog.value = false }, {name: String ->
            friends.put(name, HashMap())
            createFriendDialog.value = false
            couroutineScope.launch{userSettings.updateFriendsSubjects(friends)}

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
                    FriendItem(friend.key, {
                        friendName = friend.key
                        shouldShowDialog.value = true;
                        println("creating friend ${friend.key}")
                    }, {
                        val updatedFriends = HashMap(friends)
                        updatedFriends.remove(friend.key)
                        couroutineScope.launch {
                            userSettings.updateFriendsSubjects(updatedFriends)
                        }
                    })
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier
                        .padding(16.dp, 0.dp)
                        .height(60.dp)
                        .weight(1f),
                    onClick = {
                    createFriendDialog.value = true
                }) {
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Favorite",
                            modifier = Modifier
                                .size(30.dp)
                                .weight(1f)

                        )
                        Text(
                            text= "Freund",
                            modifier =  Modifier
                                .weight(2f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Button(

                    modifier = Modifier
                        .padding(16.dp, 0.dp)
                        .height(60.dp)
                        .weight(1.5f),
                    onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet.value = false
                        }
                    }
                }) {
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Favorite",
                            modifier = Modifier
                                .size(30.dp)
                                .weight(1f)

                        )
                        Text(
                            text= "Fertig",
                            modifier =  Modifier
                                .weight(2f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3ExpressiveApi::class, FlowPreview::class,
    ExperimentalMaterial3Api::class
)
@SuppressLint("MutableCollectionMutableState", "CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Settings(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val userSettings = UserSettings.getInstance(context.applicationContext)
    val showTeacher by userSettings.showTeacher.collectAsState(initial = false)
    val schoolID by userSettings.schoolID.collectAsState(initial = "")
    val password by userSettings.password.collectAsState(initial = "")
    val username by userSettings.username.collectAsState(initial = "")
    var Kurse by remember { mutableStateOf<ArrayList<Kurs>?>(ArrayList()) }
    val FriendsListToggle = remember { mutableStateOf(false) }
    val OwnSubjectDialogToggle = remember { mutableStateOf(false) }
    val couroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        Kurse = getKurse(userSettings, "/mobil/mobdaten/Klassen.xml")

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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
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
                Button(onClick = {OwnSubjectDialogToggle.value = true}) {
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Favorite",
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Text("Ändern")
                    }
                }
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
                Button(onClick = {FriendsListToggle.value = true}) {
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Favorite",
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Text("Ändern")
                    }
                }
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
                Text("Ende des Tages")
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {FriendsListToggle.value = true}, enabled = false) {
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Favorite",
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Text("Ändern")
                    }
                }
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
                    var sID by remember { mutableStateOf("") }

                    LaunchedEffect(Unit) {
                        sID = userSettings.schoolID.first()
                    }

                    TextField(
                        value = sID,
                        onValueChange = {
                            sID = it
                        },

                        label = { Text("Schul-ID") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Web,
                                contentDescription = "URL"
                            )
                        },
                        singleLine = true,
                    )
                    LaunchedEffect(sID) {
                        snapshotFlow { sID }
                            .debounce(500)
                            .collect { userSettings.updateSchoolID(it) }
                    }
                }
        }

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
                var uname by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    uname = userSettings.username.first()
                }

                TextField(
                    value = uname,
                    onValueChange = {
                        uname = it
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
                LaunchedEffect(uname) {
                    snapshotFlow { uname }
                        .debounce(500) // wait 500ms after the last keystroke
                        .collect { userSettings.updateUsername(it) }
                }
            }
        }

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
                var pwd by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    pwd = userSettings.password.first()
                }

                TextField(
                    visualTransformation = PasswordVisualTransformation(),
                    value = pwd,
                    onValueChange = {
                        pwd = it
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
                LaunchedEffect(pwd) {
                    snapshotFlow { pwd }
                        .debounce(500) // wait 500ms after the last keystroke
                        .collect { userSettings.updatePassword(it) }
                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    IndiwareNativeTheme {
        Settings()
    }
}