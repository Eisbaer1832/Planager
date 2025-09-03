package com.example.indiwarenative

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import com.example.indiwarenative.data.backend.getKurse
import com.example.indiwarenative.components.FriendCreateDialog
import com.example.indiwarenative.components.FriendItem
import com.example.indiwarenative.components.NavBar
import com.example.indiwarenative.components.SettingsCardDropdown
import com.example.indiwarenative.components.SettingsCardEdit
import com.example.indiwarenative.components.SettingsCardInput
import com.example.indiwarenative.components.SubjectDialog
import com.example.indiwarenative.components.TopBar
import com.example.indiwarenative.data.DataSharer.FilterClass
import com.example.indiwarenative.data.Kurs
import com.example.indiwarenative.data.UserSettings
import com.example.indiwarenative.data.backend.getAllClasses
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
    userSettings: UserSettings,
    allClasses: Array<String>,

    ) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val friends by userSettings.friendsSubjects.collectAsState(initial = HashMap())
    val friendsClasses by userSettings.friendsClass.collectAsState(initial = HashMap())
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
                    FriendItem(
                        //friends[friend.key].get("class") geht leider nicht :(
                        friend.key, friendsClasses.get(friend.key)?:"",
                        {
                        friendName = friend.key
                        shouldShowDialog.value = true;
                    }, {selected -> couroutineScope.launch{
                            var current = userSettings.friendsSubjects.first()
                            val newMap = HashMap<String, String>()
                            FilterClass = selected
                            newMap.put(friend.key, selected)
                            userSettings.updateFriendsClass( newMap)
                        }
                    }, {
                        val updatedFriends = HashMap(friends)
                        updatedFriends.remove(friend.key)
                        couroutineScope.launch {
                            userSettings.updateFriendsSubjects(updatedFriends)
                        }
                    },allClasses)
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
    val ownClass by userSettings.ownClass.collectAsState(initial = "")
    var Kurse by remember { mutableStateOf<ArrayList<Kurs>?>(ArrayList()) }
    var allClasses: Array<String> by remember { mutableStateOf(arrayOf(String())) }

    val FriendsListToggle = remember { mutableStateOf(false) }
    val OwnSubjectDialogToggle = remember { mutableStateOf(false) }
    val couroutineScope = rememberCoroutineScope()
    val topShape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)
    val bottomShape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp)
    val roundShape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp)
    val neutralShape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp)

    LaunchedEffect(Unit, FilterClass) {
        allClasses = getAllClasses(userSettings, "/mobil/mobdaten/Klassen.xml")?: arrayOf(String())
        Kurse = getKurse(userSettings, "/mobil/mobdaten/Klassen.xml")

    }

    if (OwnSubjectDialogToggle.value) {
        SubjectDialog(shouldShowDialog = OwnSubjectDialogToggle, Kurse, userSettings, true)
    }
    if (FriendsListToggle.value) {
        FriendsList(FriendsListToggle, Kurse,userSettings, allClasses)
    }



    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("App Einstellungen", style = MaterialTheme.typography.headlineMediumEmphasized)

        var hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        // notification permission stuff
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Benachrichtigungen aktiviert", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Benachrichtigungen deaktiviert", Toast.LENGTH_SHORT).show()
            }
        }

        if (!hasPermission) SettingsCardEdit("Benachrichtigungen", roundShape, Icons.Default.Check, "Erlauben", 30.dp, ) {permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)}




        SettingsCardEdit("Eigene Fächer", topShape, buttonText = "") {
            FilterClass = ownClass
            OwnSubjectDialogToggle.value = true
        }
        SettingsCardDropdown("Jahrgang",bottomShape,allClasses, default= ownClass, onclick =  {
            selected -> couroutineScope.launch{
                userSettings.updateOwnClass(selected)
                userSettings.updateOwnSubjects(HashMap())
            }}
        )
        SettingsCardEdit("Fächer von Freunden",roundShape, buttonText = "") { FriendsListToggle.value = true }


        var checked by remember { mutableStateOf(true) }

        Card(
            shape = roundShape,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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

        val schoolID by userSettings.schoolID.collectAsState(initial = "")
        SettingsCardInput(
            topShape,
            userSettings,
            "Nutzername",
            Icons.Filled.Web,
            schoolID,
            { settings ->
                settings.schoolID.first() // async load
            },
            { value, settings ->
                settings.updateSchoolID(value) // async save
            }
        )

        val username by userSettings.password.collectAsState(initial = "")
        SettingsCardInput(
            neutralShape,
            userSettings,
            "Schul ID",
            Icons.Filled.Person,
            username,
            { settings ->
                settings.username.first() // async load
            },
            { value, settings ->
                settings.updateUsername(value) // async save
            }
        )

        val pwd by userSettings.password.collectAsState(initial = "")
        SettingsCardInput(
            bottomShape,
            userSettings,
            "Passwort",
            Icons.Filled.Password,
            pwd,
            { settings ->
               settings.password.first() // async load
            },
            { value, settings ->
                settings.updatePassword(value) // async save
            },
            true
        )
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