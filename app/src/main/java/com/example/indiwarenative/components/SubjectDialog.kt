package com.example.indiwarenative.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.indiwarenative.Kurs
import com.example.indiwarenative.UserSettings
import kotlinx.coroutines.flow.compose
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.apply


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDialog(
    shouldShowDialog: MutableState<Boolean>,
    Kurse: ArrayList<Kurs>?,
    userSettings: UserSettings,
    own: Boolean,
    friend: String = ""
) {
    if (shouldShowDialog.value) {
        val title = if (own) "Eigene Fächer" else "Freund Fächer"
        val couroutineScope = rememberCoroutineScope()
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
                        Text(title)
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
                                            var checked by remember { mutableStateOf(status.value.get(Kurse[i].subject) == true) }
                                            Switch(
                                                checked = checked,
                                                onCheckedChange = {
                                                    checked = it
                                                    status.value.put(Kurse[i].subject, checked)
                                                    if (own) {
                                                        couroutineScope.launch { userSettings.updateOwnSubjects(status.value) }
                                                    }else {
                                                        println("friend $friend: " + allFriends.value.get(friend))
                                                        //TODO save the actual damn thing
                                                    }

                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
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