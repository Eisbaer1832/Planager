package com.capputinodevelopment.planager.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Room
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.lesson
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.DayOfWeek


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SubjectCreateSheet (
    showBottomSheet: MutableState<Boolean>,
    userSettings: UserSettings,
    pos: Int,
    day: DayOfWeek
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var savedCustomSubjects = userSettings.customSubjects.collectAsState(mutableMapOf())

    val subject = TextFieldState("")
    val teacher = TextFieldState("")
    val room = TextFieldState("")

    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                ) {

                OutlinedTextField(
                    leadingIcon = {Icon(Icons.Default.School, "Subject Icon") },
                    label = {Text("Fach")},
                    state = subject,
                )

                Row {
                    OutlinedTextField(
                        leadingIcon = {Icon(Icons.Default.Person, "Person Icon") },
                        label = {Text("Lehrerkürzel")},
                        state = teacher,
                    )
                    OutlinedTextField(
                        leadingIcon = {Icon(Icons.Default.Room, "Room Icon") },
                        label = {Text("Raum")},
                        state = room,
                    )
                }
                Button(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth(),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            val newSubject = lesson(pos, teacher.text.toString(), subject.text.toString(), room.text.toString())
                            val posMap = savedCustomSubjects.value[pos]
                            println("day to create: " + day)
                            posMap?.put(day, newSubject)
                            val newCustomSubjects = savedCustomSubjects.value
                            newCustomSubjects.put(pos, posMap?:mutableMapOf())?:mutableMapOf()

                            scope.launch {
                                userSettings.updateCustomSubjects(newCustomSubjects)
                                println("subjects: " + newCustomSubjects)
                                if (!sheetState.isVisible) {
                                    showBottomSheet.value = false
                                }
                            }
                        }
                    }) {
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Speichern",
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Text(
                            text= "Fertig",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
