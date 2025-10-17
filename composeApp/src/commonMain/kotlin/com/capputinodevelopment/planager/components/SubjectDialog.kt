package com.capputinodevelopment.planager.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupWork
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.glance.appwidget.updateAll
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.capputinodevelopment.planager.DayWidget
import com.capputinodevelopment.planager.RoomWidget
import com.capputinodevelopment.planager.data.Kurs
import com.capputinodevelopment.planager.data.UserSettings
import kotlinx.coroutines.launch

@Composable
fun ShowLessonList(modifier: Modifier = Modifier, userSettings: UserSettings, kurse: ArrayList<Kurs>, status: State<HashMap<String, Boolean>>, own: Boolean, friend: String) {
    val couroutineScope = rememberCoroutineScope()
    val allFriends = userSettings.friendsSubjects.collectAsState(initial = HashMap())

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            println("Kurse2: " + kurse)
            kurse.size.let {
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
                                    text = kurse[i].subject + " " + kurse[i].teacher
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                var checked by remember { mutableStateOf(status.value.get(kurse[i].subject) == true) }
                                Switch(
                                    checked = checked,
                                    onCheckedChange = {
                                        checked = it
                                        status.value.put(kurse[i].subject, checked)
                                        if (own) {
                                            couroutineScope.launch {
                                                userSettings.updateOwnSubjects(status.value)
                                            }
                                        } else {
                                            println("friend $friend: " + allFriends.value.get(friend))
                                            allFriends.value.put(friend, status.value)
                                            couroutineScope.launch {
                                                userSettings.updateFriendsSubjects(
                                                    allFriends.value
                                                )
                                            }
                                        }

                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier= Modifier.height(80.dp))
            }
        }
    }
}

enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    LESSONS("lessons", "Unterricht", Icons.Default.School),
    AGS("ags", "AGs", Icons.Default.GroupWork),
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
    userSettings: UserSettings,
    kurse: ArrayList<Kurs>,
    ags: ArrayList<Kurs>,
    status: State<HashMap<String, Boolean>>,
    own: Boolean,
    friend: String

) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.LESSONS -> ShowLessonList(
                        modifier,
                        userSettings = userSettings,
                        kurse = kurse,
                        status = status,
                        own = own,
                        friend = friend
                    )
                    Destination.AGS -> ShowLessonList(
                        modifier,
                        userSettings = userSettings,
                        kurse = ags,
                        status = status,
                        own = own,
                        friend = friend
                    )
                }
            }
        }
    }
}


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SubjectDialog(
    shouldShowDialog: MutableState<Boolean>,
    Kurse: ArrayList<Kurs>?,
    ags: ArrayList<Kurs>?,
    userSettings: UserSettings,
    own: Boolean,
    friend: String = ""
) {
    if (shouldShowDialog.value) {
        val context = LocalContext.current
        val title = if (own) "Eigene Fächer" else "${friend}s Fächer"
        val coroutineScope = rememberCoroutineScope()
        val allFriends = userSettings.friendsSubjects.collectAsState(initial = HashMap())

        val status: State<HashMap<String, Boolean>> = if (!own) {
            println("not own subjects")
            println("friend $friend: " + allFriends.value)
            mutableStateOf(allFriends.value[friend] ?: HashMap())
        } else {
            userSettings.ownSubjects.collectAsState(initial = HashMap())
        }

        BasicAlertDialog(
            onDismissRequest = {
                shouldShowDialog.value = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            properties = DialogProperties(), content = {
                val navController = rememberNavController()
                val startDestination = Destination.LESSONS
                var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            shouldShowDialog.value = false
                            coroutineScope.launch {
                                println("updating widgets")
                                //RoomWidget().updateAll(context.applicationContext)
                                //DayWidget().updateAll(context.applicationContext)
                            }
                        }) {
                            Icon(Icons.Filled.Save, "Save")
                        }
                    }
                ) {innerPadding ->
                    Surface(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .wrapContentHeight(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        println("Kurse: " + Kurse)
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text(title)
                            PrimaryTabRow(selectedTabIndex = selectedDestination) {
                                Destination.entries.forEachIndexed { index, destination ->
                                    Tab(
                                        selected = selectedDestination == index,
                                        onClick = {
                                            navController.navigate(route = destination.route)
                                            selectedDestination = index
                                        },
                                        text = {
                                            Text(
                                                text = destination.label,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        })
                                }
                            }

                            AppNavHost(
                                navController,
                                modifier = Modifier,
                                userSettings = userSettings,
                                kurse = Kurse ?: arrayListOf(),
                                ags = ags ?: arrayListOf(),
                                status = status,
                                own = own,
                                friend = friend,
                                startDestination = startDestination,
                            )


                        }
                    }
                }
            })
    }
}