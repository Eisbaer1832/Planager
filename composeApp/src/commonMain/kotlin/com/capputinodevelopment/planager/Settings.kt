package com.capputinodevelopment.planager

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalUriHandler
import com.capputinodevelopment.planager.components.CheckCredentials
import com.capputinodevelopment.planager.data.backend.getKurse
import com.capputinodevelopment.planager.components.FriendsList
import com.capputinodevelopment.planager.components.LicenseDialog
import com.capputinodevelopment.planager.components.SettingsCardDropdown
import com.capputinodevelopment.planager.components.SettingsCardEdit
import com.capputinodevelopment.planager.components.SettingsCardInput
import com.capputinodevelopment.planager.components.SubjectDialog
import com.capputinodevelopment.planager.data.DataSharer.AGs
import com.capputinodevelopment.planager.data.DataSharer.FilterClass
import com.capputinodevelopment.planager.data.DataSharer.Kurse
import com.capputinodevelopment.planager.data.DataSharer.bottomShape
import com.capputinodevelopment.planager.data.DataSharer.neutralShape
import com.capputinodevelopment.planager.data.DataSharer.topShape
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.backend.fixDay
import com.capputinodevelopment.planager.data.backend.getAllClasses
import com.capputinodevelopment.planager.data.getToday
import kotlinx.coroutines.flow.first
import org.jetbrains.compose.resources.painterResource
import planager.composeapp.generated.resources.Res
import planager.composeapp.generated.resources.compose_multiplatform

@Composable
expect fun NotificationPermissionCheck()

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Settings(modifier: Modifier = Modifier, snackbarHostState: SnackbarHostState, userSettings: UserSettings) {
    val showTeacher by userSettings.showTeacher.collectAsState(initial = false)
    val ownClass by userSettings.ownClass.collectAsState(initial = "")
    var allClasses: Array<String> by remember { mutableStateOf(arrayOf()) }
    val current = fixDay(getToday())
    val FriendsListToggle = remember { mutableStateOf(false) }
    val OwnSubjectDialogToggle = remember { mutableStateOf(false) }
    val licenseDialogToggle = remember { mutableStateOf(false) }

    val couroutineScope = rememberCoroutineScope()
    val onboarding by userSettings.onboarding.collectAsState(initial = null)

    LaunchedEffect(Unit, FilterClass) {
        allClasses = getAllClasses(userSettings, "/mobil/mobdaten/Klassen.xml")?: arrayOf<String>()
        if (Kurse.isEmpty()) {
            Kurse = getKurse(userSettings, current.dayOfWeek, null)?: ArrayList()
        }
        if (AGs.isEmpty()) {
            AGs = getKurse(userSettings, current.dayOfWeek, "AG",)?: ArrayList()
        }

    }
    LaunchedEffect(onboarding) {
        if (onboarding == true) {
            println("doing onboarding")
            //TODO Reimplement Onboarding
            //context.startActivity(Intent(context, Onboarding::class.java))
        }
    }
    if (OwnSubjectDialogToggle.value) {
        SubjectDialog(shouldShowDialog = OwnSubjectDialogToggle, Kurse, AGs, userSettings, true)
    }
    if (licenseDialogToggle.value) {
        LicenseDialog(licenseDialogToggle)
    }
    if (FriendsListToggle.value) {
        FriendsList(FriendsListToggle, Kurse,AGs,userSettings, allClasses)
    }



    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 0.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("App Einstellungen", style = MaterialTheme.typography.headlineMediumEmphasized)


        NotificationPermissionCheck()


        SettingsCardEdit(
            "Eigene Fächer", topShape, buttonText = "",
            onclick = {
                FilterClass = ownClass
                OwnSubjectDialogToggle.value = true


                couroutineScope.launch {
                    Kurse = getKurse(userSettings, current.dayOfWeek, null)?: ArrayList()
                }
            },
        )
        SettingsCardDropdown("Jahrgang / Klasse",neutralShape,allClasses, default= ownClass, onclick =  {
            selected -> couroutineScope.launch{
                FilterClass = selected
                userSettings.updateOwnClass(selected)
                userSettings.updateOwnSubjects(HashMap())
            }}
        )
        SettingsCardEdit(
            "Fächer von Freunden",
            neutralShape,
            buttonText = "",
            onclick = { FriendsListToggle.value = true },
        )


        var checked by remember { mutableStateOf(true) }

        Card(
            shape = bottomShape,
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
                        couroutineScope.launch{
                            userSettings.updateShowTeachers(checked)
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Server Daten", style = MaterialTheme.typography.headlineMedium)

        val schoolID by userSettings.schoolID.collectAsState(initial = "")
        SettingsCardInput(
            topShape,
            userSettings,
            "Schul ID",
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
            "Nutzername",
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
        CheckCredentials(snackbarHostState = snackbarHostState, {  }, userSettings)

        Spacer(Modifier.height(20.dp))
        Text("Sonstiges", style = MaterialTheme.typography.headlineMedium)

        val uriHandler = LocalUriHandler.current
        SettingsCardEdit(
            "Spenden", topShape, buttonIcon = Icons.Default.Favorite, buttonText = "", leadingIcon = painterResource(Res.drawable.compose_multiplatform),
            onclick = {
                uriHandler.openUri("https://ko-fi.com/capputinodevelopment")
            },
        )
        SettingsCardEdit(
            "Einrichtung neustarten",
            neutralShape,
            buttonIcon = Icons.Default.Replay,
            buttonText = "",
            onclick = {
                couroutineScope.launch {userSettings.updateOnboarding(true)}
            },
        )

        SettingsCardEdit(
            "Lizenzen", bottomShape, buttonText = "",
            onclick = {
                licenseDialogToggle.value = true
            },
            buttonIcon = Icons.Default.Info
        )
    }

}
