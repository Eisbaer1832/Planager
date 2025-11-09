
package com.capputinodevelopment.planager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.capputinodevelopment.planager.Screens.DayView
import com.capputinodevelopment.planager.Screens.ResearchView
import com.capputinodevelopment.planager.Screens.Settings
import com.capputinodevelopment.planager.Screens.WeekViewWrapper
import com.capputinodevelopment.planager.components.NavBar
import com.capputinodevelopment.planager.components.SearchDaySwitch
import com.capputinodevelopment.planager.components.TopBar
import com.capputinodevelopment.planager.data.RegisterWorker
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.fetchWeekType
import com.capputinodevelopment.planager.ui.theme.IndiwareNativeTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(window)
        setContent {
            IndiwareNativeTheme {
                RegisterWorker()
                var currentScreen by remember { mutableIntStateOf(0) }
                var editSubjects by remember { mutableStateOf(false) }

                val userSettings = UserSettings.getInstance(applicationContext)
                val defaultScreen = userSettings.defaultScreen.collectAsState("")
                LaunchedEffect(defaultScreen.value) {
                    currentScreen = if (defaultScreen.value == "Tagesplan") 0 else 1
                }

                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
                    floatingActionButton = {
                        when(currentScreen){
                            2 -> SearchDaySwitch()
                        }
                    },
                    topBar = {
                        when (currentScreen) {
                            0 -> TopBar("Tagesplan", true, editSubjects) {}
                            1 -> TopBar("Wochenplan - " + fetchWeekType(), true, editSubjects, true) {
                                editSubjects = !editSubjects
                                println("EditSubjects toggled: $editSubjects")
                            }
                            3 -> TopBar("Einstellungen", false, editSubjects) {}
                        }
                    }, bottomBar = {
                        NavBar(currentScreen) { currentScreen = it } }
                ){ innerPadding ->
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            val direction = if (targetState > initialState) 1 else -1
                            slideInHorizontally { width -> width * direction }.togetherWith(
                                slideOutHorizontally { width -> -width * direction }
                            )
                        }
                    ) { screen ->
                        when (screen) {
                            0 -> DayView(modifier = Modifier.padding(innerPadding))
                            1 -> WeekViewWrapper(modifier = Modifier.padding(innerPadding), editSubjects)
                            2 -> ResearchView(modifier = Modifier.padding(innerPadding))
                            3 -> Settings(modifier = Modifier.padding(innerPadding),snackbarHostState)
                        }
                    }

                }
            }
        }
    }
}

