package com.example.indiwarenative
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.twotone.Password
import androidx.compose.material.icons.twotone.School
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.indiwarenative.components.SettingsCardDropdown
import com.example.indiwarenative.components.SettingsCardEdit
import com.example.indiwarenative.components.SettingsCardInput
import com.example.indiwarenative.components.SubjectDialog
import com.example.indiwarenative.data.DataSharer.bottomShape
import com.example.indiwarenative.data.DataSharer.neutralShape
import com.example.indiwarenative.data.DataSharer.topShape
import com.example.indiwarenative.data.Kurs
import com.example.indiwarenative.data.UserSettings
import com.example.indiwarenative.data.backend.getAllClasses
import com.example.indiwarenative.data.backend.getKurse
import com.example.indiwarenative.ui.theme.IndiwareNativeTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// This file is based off of https://github.com/ahmmedrejowan/OnboardingScreen-JetpackCompose

class Onboarding : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IndiwareNativeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Onboarding(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SecondPageInput() {
    val context = LocalContext.current
    val userSettings = UserSettings.getInstance(context.applicationContext)
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


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ThirdPageInput() {
    val context = LocalContext.current
    val userSettings = UserSettings.getInstance(context.applicationContext)
    val schoolID by userSettings.schoolID.collectAsState(initial = "")
    val ownClass by userSettings.ownClass.collectAsState(initial = "")
    var allClasses: Array<String> by remember { mutableStateOf(arrayOf(String())) }
    var loading: Boolean by remember { mutableStateOf(false) }
    val OwnSubjectDialogToggle = remember { mutableStateOf(false) }
    val couroutineScope = rememberCoroutineScope()
    var Kurse by remember { mutableStateOf<ArrayList<Kurs>?>(ArrayList()) }
    var localFilterClass by remember { mutableStateOf("") }

    LaunchedEffect(Unit, localFilterClass) {
        loading = true
        allClasses = getAllClasses(userSettings, "/mobil/mobdaten/Klassen.xml")?: arrayOf(String())
        Kurse = getKurse(userSettings, "/mobil/mobdaten/Klassen.xml", localFilterClass)
        loading = false
    }
    if (OwnSubjectDialogToggle.value) {
        SubjectDialog(shouldShowDialog = OwnSubjectDialogToggle, Kurse, userSettings, true)
    }
    if (loading) {
        Column (
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){ LoadingIndicator() }

    }else {
        SettingsCardEdit("Eigene Fächer", topShape, buttonText = "") {

            OwnSubjectDialogToggle.value = true
        }
        SettingsCardDropdown(
            "Jahrgang",
            bottomShape,
            allClasses,
            default = ownClass,
            onclick = { selected ->
                couroutineScope.launch {
                    localFilterClass = selected
                    userSettings.updateOwnClass(selected)
                    userSettings.updateOwnSubjects(HashMap())
                }
            }
        )
    }

}

sealed class OnboardingModel (
    val image: ImageVector,
    val title: String,
    val description: String,
    val input: @Composable () -> Unit = {},
) {

    data object FirstPage : OnboardingModel(
        image = Icons.TwoTone.School,
        title = "Wilkommen bei deinem persönlichen Stundenplaner!",
        description = "Nimm dir kurz Zeit um alles einzurichten",
    )

    data object SecondPage : OnboardingModel(
        image = Icons.TwoTone.Password,
        title = "Gib deine Nutzerdaten ein",
        description = "Du solltest sie von deiner Schule erhalten haben",
        input = {
            SecondPageInput()
        }
    )

    @RequiresApi(Build.VERSION_CODES.O)
    data object ThirdPages : OnboardingModel(
        image = Icons.TwoTone.Settings,
        title = "Wähle nun deine Klasse und ggf. Kurse",
        description = "Keine Sorge, du kannst diese jederzeit in den Einstellungen ändern",
        input = {
            ThirdPageInput()
        }
    )


}

@Composable
fun Page(onboardingModel: OnboardingModel) {

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {


        Icon(
            onboardingModel.image,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .size(100.dp)
                .padding(40.dp, 0.dp),
        )

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
                .background(color = if(it == currentPage) selectedColor else unselectedColor)

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
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Onboarding(name: String, modifier: Modifier = Modifier) {
    val pages = listOf(
        OnboardingModel.FirstPage, OnboardingModel.SecondPage, OnboardingModel.ThirdPages
    )

    val pagerState = rememberPagerState(initialPage = 0) {
        pages.size
    }
    val buttonState = remember {
        derivedStateOf {
            when (pagerState.currentPage) {
                0 -> listOf("", "Weiter")
                1 -> listOf("Zurück", "Weiter")
                2 -> listOf("Zurück", "Start")
                else -> listOf("", "")
            }
        }
    }

    val scope = rememberCoroutineScope()

    Scaffold(bottomBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart) { if (buttonState.value[0].isNotEmpty()) {
                ButtonUi (text = buttonState.value[0],
                    backgroundColor = Color.Transparent,
                    textColor = Color.Gray) {
                    scope.launch {
                        if (pagerState.currentPage > 0) {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                }
            }
            }
            Box(modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center) {
                IndicatorUI(pageSize = pages.size, currentPage = pagerState.currentPage)
            }

            val context = LocalContext.current
            val userSettings = UserSettings.getInstance(context.applicationContext)
            Box(modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd) {
                ButtonUi (text = buttonState.value[1],
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    textColor = MaterialTheme.colorScheme.onPrimary) {

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
    }, content = {
        Column(Modifier.padding(it)) {
            HorizontalPager(state = pagerState) { index ->
                Page(onboardingModel = pages[index])
            }
        }
    })
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    IndiwareNativeTheme {
        Onboarding("Android")
    }
}