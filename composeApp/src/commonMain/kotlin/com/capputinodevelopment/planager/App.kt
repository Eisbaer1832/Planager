package com.capputinodevelopment.planager

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.capputinodevelopment.planager.components.NavBar
import com.capputinodevelopment.planager.components.SliderToolBar
import com.capputinodevelopment.planager.components.TopBar
import com.capputinodevelopment.planager.data.UserSettings
import com.russhwolf.settings.Settings
import org.jetbrains.compose.ui.tooling.preview.Preview
expect fun provideSettings(): Settings
@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf(0) }
        val snackbarHostState = remember { SnackbarHostState() }

        val settings = provideSettings()
        val userSettings = UserSettings.getInstance(settings)

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            floatingActionButton = {when(currentScreen){
                2 -> SliderToolBar()
            }},
            topBar = {
                when (currentScreen) {
                    0 -> TopBar("Tagesplan", true, userSettings)
                    1 -> TopBar("Wochenplan", true, userSettings)
                    3 -> TopBar("Einstellungen", false, userSettings)
                }
            }, bottomBar = {
                NavBar(currentScreen) { currentScreen = it } }
        ){ innerPadding ->
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    slideInHorizontally { width -> width }.togetherWith(slideOutHorizontally { width -> -width })
                }
            ) { screen ->
                when (screen) {
                    0 -> Greeting(name = "Android", modifier = Modifier.padding(innerPadding), userSettings)
                    1 -> WeekView(modifier = Modifier.padding(innerPadding), userSettings)
                    2 -> ResearchView(name="Recherche", modifier = Modifier.padding(innerPadding), userSettings)
                    3 -> Settings(modifier = Modifier.padding(innerPadding), snackbarHostState, userSettings)
                }
            }

        }
    }
}