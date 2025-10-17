package com.capputinodevelopment.planager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Room
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.twotone.Password
import androidx.compose.material.icons.twotone.School
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material.icons.twotone.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capputinodevelopment.planager.components.CheckCredentials
import com.capputinodevelopment.planager.components.SettingsCardDropdown
import com.capputinodevelopment.planager.components.SettingsCardEdit
import com.capputinodevelopment.planager.components.SettingsCardInput
import com.capputinodevelopment.planager.components.SubjectDialog
import com.capputinodevelopment.planager.data.DataSharer.AGs
import com.capputinodevelopment.planager.data.DataSharer.Kurse
import com.capputinodevelopment.planager.data.DataSharer.bottomShape
import com.capputinodevelopment.planager.data.DataSharer.neutralShape
import com.capputinodevelopment.planager.data.DataSharer.topShape
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.backend.fetchTimetable
import com.capputinodevelopment.planager.data.backend.getAllClasses
import com.capputinodevelopment.planager.data.backend.getKurse
import com.capputinodevelopment.planager.data.getToday
import com.capputinodevelopment.planager.ui.theme.IndiwareNativeTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// This file is based off of https://github.com/ahmmedrejowan/OnboardingScreen-JetpackCompose

@Composable
fun SecondPageInput(
    onValidationChanged: (Boolean) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val userSettings = UserSettings.getInstance(context.applicationContext)
    rememberCoroutineScope()
    val schoolID by userSettings.schoolID.collectAsState(initial = "")
    SettingsCardInput(
        topShape,
        userSettings,
        "Schul ID",
        Icons.Filled.Web,
        schoolID,
        { settings -> settings.schoolID.first() },
        { value, settings -> settings.updateSchoolID(value) }
    )

    val username by userSettings.username.collectAsState(initial = "")
    SettingsCardInput(
        neutralShape,
        userSettings,
        "Nutzername",
        Icons.Filled.Person,
        username,
        { settings -> settings.username.first() },
        { value, settings ->
            settings.updateUsername(value)
        }
    )

    val pwd by userSettings.password.collectAsState(initial = "")
    SettingsCardInput(
        bottomShape,
        userSettings,
        "Passwort",
        Icons.Filled.Password,
        pwd,
        { settings -> settings.password.first() },
        { value, settings -> settings.updatePassword(value) },
        true
    )
    CheckCredentials(snackbarHostState = snackbarHostState, onValidationChanged = onValidationChanged, context)
}
@Composable
fun ThirdPageInput() {
    val context = LocalContext.current
    val userSettings = UserSettings.getInstance(context.applicationContext)

    val ownClass by userSettings.ownClass.collectAsState(initial = "")
    val schoolID by userSettings.schoolID.collectAsState(initial = "")
    val username by userSettings.username.collectAsState(initial = "")
    val password by userSettings.password.collectAsState(initial = "")

    var allClasses by remember { mutableStateOf(emptyArray<String>()) }
    var loading by remember { mutableStateOf(false) }
    val OwnSubjectDialogToggle = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var localFilterClass by remember { mutableStateOf("") }
    val current = getToday()


    LaunchedEffect(schoolID, username, password, localFilterClass) {
        if (schoolID.isBlank() || username.isBlank() || password.isBlank()) return@LaunchedEffect
        loading = true
        try {
            Kurse = getKurse(userSettings, current.dayOfWeek, localFilterClass, context) ?: ArrayList()
            AGs = getKurse(userSettings, current.dayOfWeek, "AG", context) ?: ArrayList()
            allClasses = getAllClasses(userSettings, "/mobil/mobdaten/Klassen.xml", context) ?: arrayOf()
            println("all classes: ${allClasses.joinToString()}  password length=${password.length}")
        } finally {
            loading = false
        }
    }
    when {
        schoolID.isBlank() || username.isBlank() || password.isBlank()  -> {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoadingIndicator()
            }
        }
        loading -> {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) { LoadingIndicator() }
        }
        else -> {
            if (OwnSubjectDialogToggle.value) {
                SubjectDialog(OwnSubjectDialogToggle, Kurse, AGs, userSettings, true)
            }
            SettingsCardDropdown(
                "Jahrgang / Klasse",
                topShape,
                allClasses,
                default = ownClass,
                onclick = { selected ->
                    coroutineScope.launch {
                        localFilterClass = selected
                        userSettings.updateOwnClass(selected)
                        userSettings.updateOwnSubjects(HashMap())
                    }
                }
            )
            SettingsCardEdit(
                "Eigene Fächer", bottomShape,
                buttonText = "",
                onclick = {
                    localFilterClass = ownClass
                    OwnSubjectDialogToggle.value = true
                }
            )
        }
    }
}


@Composable
fun FourthPageInput() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .height(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            onClick = {
                coroutineScope.launch {
                    GlanceAppWidgetManager(context).requestPinGlanceAppWidget(
                        receiver = RoomWidgetReceiver::class.java,
                        preview = RoomWidget(),
                        previewState = DpSize(245.dp, 115.dp)
                    )
                }
            }
        ) {
            Icon(Icons.Default.Room, "")
            Text("Nächster Raum")
        }
        Button(
            onClick = {
                coroutineScope.launch {
                    GlanceAppWidgetManager(context).requestPinGlanceAppWidget(
                        receiver = DayWidgetReceiver::class.java,
                        preview = DayWidget(),
                        previewState = DpSize(245.dp, 115.dp)
                    )
                }
            }
        ) {
            Icon(Icons.Default.ViewDay, "")
            Text("Unterricht des Tages")
        }
    }

}


@Composable
fun FithPageInput() {
    LocalContext.current
    rememberCoroutineScope()
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .height(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val uriHandler = LocalUriHandler.current
        Button(
            onClick = {
                uriHandler.openUri("https://ko-fi.com/capputinodevelopment")
            },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    modifier = Modifier
                        .size(30.dp)
                )
                Text("Spenden")
            }
        }
    }

}

sealed class OnboardingModel(
    val image: ImageVector,
    val title: String,
    val description: String,
    val input: @Composable () -> Unit = {},
    val gif: Int? = null,
) {
    data object FirstPage : OnboardingModel(
        image = Icons.TwoTone.School,
        title = "Willkommen bei deinem persönlichen Stundenplaner!",
        description = "Nimm dir kurz Zeit um alles einzurichten."
    )

    class SecondPage(val onValidationChanged: (Boolean) -> Unit, snackbarHostState: SnackbarHostState) : OnboardingModel(
        image = Icons.TwoTone.Password,
        title = "Gib deine Nutzerdaten ein",
        description = "Du solltest sie bereits von deiner Schule erhalten haben.",
        input = { SecondPageInput(onValidationChanged, snackbarHostState) }
    )

    data object ThirdPages : OnboardingModel(
        image = Icons.TwoTone.Settings,
        title = "Wähle nun deine Klasse und ggf. Kurse",
        description = "Keine Sorge, du kannst diese jederzeit in den Einstellungen ändern.",
        input = { ThirdPageInput() }
    )

    data object FourthPage : OnboardingModel(
        image = Icons.TwoTone.Widgets,
        title = "Widgets",
        description = "Mit Widgets kannst du dir ganz bequem deinen nächsten Raum oder den heutigen Stundenplan anzeigen lassen.",
        input = { FourthPageInput() }
    )

    data object FithPage : OnboardingModel(
        image = Icons.TwoTone.Widgets,
        gif = R.drawable.sparkle_mug,
        title = "Unterstütze die Entwicklung von Planager",
        description = "Planager ist ein für dich komplett kostenloses Hobbyprojekt! Wenn du mich unterstützen möchtest, spende doch gerne einen Kaffee!",
        input = { FithPageInput() }
    )
}

@Composable
fun Page(onboardingModel: OnboardingModel) {

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {


        if (onboardingModel.gif != null) {
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxWidth()
                    .padding(40.dp, 0.dp),   //crops the image to circle shape
                painter = rememberDrawablePainter(
                    drawable = getDrawable(
                        LocalContext.current,
                        onboardingModel.gif
                    )
                ),
                contentDescription = "Loading animation",
                contentScale = ContentScale.FillWidth,
            )
        }else {
            Icon(
                onboardingModel.image,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(100.dp)
                    .padding(40.dp, 0.dp),
            )
        }

        Spacer(
            modifier = Modifier.size(50.dp)
        )

        Text(
            text = onboardingModel.title,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(15.dp)
        )

        Text(
            text = onboardingModel.description,
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp, 0.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(15.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp, 0.dp),

            ) {
            onboardingModel.input()
        }

    }


}


@Composable
fun IndicatorUI(
    pageSize: Int,
    currentPage: Int,
    selectedColor: Color = MaterialTheme.colorScheme.secondary,
    unselectedColor: Color = MaterialTheme.colorScheme.secondaryContainer
) {

    Row (horizontalArrangement = Arrangement.SpaceBetween) {
        repeat(pageSize){
            Spacer(modifier = Modifier.size(2.5.dp))

            Box(modifier = Modifier
                .height(14.dp)
                .width(width = if (it == currentPage) 32.dp else 14.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = if (it == currentPage) selectedColor else unselectedColor)

            )
            Spacer(modifier = Modifier.size(2.5.dp))

        }

    }


}

@Composable
fun ButtonUi(
    text: String = "Next",
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    fontSize: Int = 14,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick, colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor, contentColor = textColor
        ), shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = text, fontSize = fontSize.sp, style = textStyle
        )
    }
}



@Preview
@Composable
fun BackButton() {

    ButtonUi(text = "Back",
        backgroundColor = Color.Transparent,
        textColor = Color.Gray,
        textStyle = MaterialTheme.typography.bodySmall,
        fontSize = 13) {
    }


}
@Composable
fun Onboarding(name: String, modifier: Modifier = Modifier) {
    var canContinue by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val pages = listOf(
        OnboardingModel.FirstPage,
        OnboardingModel.SecondPage ({ isValid -> canContinue = isValid},snackbarHostState),
        OnboardingModel.ThirdPages,
        OnboardingModel.FourthPage,
        OnboardingModel.FithPage
    )

    val pagerState = rememberPagerState(initialPage = 0) { pages.size }
    val buttonState = remember {
        derivedStateOf {
            when (pagerState.currentPage) {
                0 -> listOf("", "Weiter")
                1 -> listOf("Zurück", "Weiter")
                2 -> listOf("Zurück", "Weiter")
                3 -> listOf("Zurück", "Weiter")
                4 -> listOf("Zurück", "Start")
                else -> listOf("", "")
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, bottom = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (buttonState.value[0].isNotEmpty()) {
                        ButtonUi(
                            text = buttonState.value[0],
                            backgroundColor = Color.Transparent,
                            textColor = Color.Gray
                        ) {
                            scope.launch {
                                if (pagerState.currentPage > 0) {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    IndicatorUI(pageSize = pages.size, currentPage = pagerState.currentPage)
                }

                val context = LocalContext.current
                val userSettings = UserSettings.getInstance(context.applicationContext)
                var enableButton = false
                if (pagerState.currentPage != 1 || canContinue) {
                    enableButton = true
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    ButtonUi(
                        text = buttonState.value[1],
                        backgroundColor = if (enableButton) MaterialTheme.colorScheme.primary else Color.Gray,
                        textColor = if (enableButton) MaterialTheme.colorScheme.onPrimary else Color.LightGray
                    ) {
                        if (enableButton) { // nur klicken wenn valid
                            scope.launch {
                                if (pagerState.currentPage < pages.size - 1) {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                } else {
                                    userSettings.updateOnboarding(false)
                                    context.startActivity(Intent(context, MainActivity::class.java))
                                    (context as? Activity)?.finish()
                                }
                            }
                        }
                    }
                }
            }
        },
        content = {
            Column(Modifier.padding(it)) {
                HorizontalPager(state = pagerState) { index ->
                    Page(onboardingModel = pages[index])
                }
            }
        }
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    IndiwareNativeTheme {
        Onboarding("Android")
    }
}