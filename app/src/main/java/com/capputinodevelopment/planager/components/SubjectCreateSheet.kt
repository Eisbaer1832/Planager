package com.capputinodevelopment.planager.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Room
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.PlusOne
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.capputinodevelopment.planager.data.DataSharer
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.lesson
import com.capputinodevelopment.planager.data.WeekType
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.toMutableList
import kotlin.collections.toMutableMap


@Composable
fun DeleteConfirmDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                tint = MaterialTheme.colorScheme.error,
                contentDescription = "Delete subject"
            )
        },
        text = {
            Text(text = "Bist du dir sicher, dass du das Fach löschen möchtest?")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Ja")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Abbrechen")
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
fun SubjectCreateSheet (
    showBottomSheet: MutableState<Boolean>,
    userSettings: UserSettings,
    day: DayOfWeek,
    lesson: lesson
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val savedCustomSubjects = userSettings.customSubjects.collectAsState(mutableMapOf())

    val subject = remember { TextFieldState(lesson.subject.filter { !it.isDigit() }) }
    val teacher = TextFieldState(lesson.teacher)
    val room = TextFieldState(lesson.room)
    val pos = lesson.pos

    val openAlertDialog = remember { mutableStateOf(false) }

    when {
        openAlertDialog.value -> {
            DeleteConfirmDialog(
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {
                    openAlertDialog.value = false

                    var newCustomSubjects = deleteSubject(lesson, savedCustomSubjects.value, pos, day)
                    if (lesson.doubleLesson) {
                        newCustomSubjects = deleteSubject(lesson, newCustomSubjects, getCompanionLesson(pos), day)
                    }

                    scope.launch {
                        userSettings.updateCustomSubjects(newCustomSubjects)
                        showBottomSheet.value = false
                    }

                },
            )
        }
    }

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

                Row (
                    modifier = Modifier
                        .height(70.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedTextField(
                        modifier = Modifier.padding(end= 16.dp),
                        leadingIcon = {Icon(Icons.Default.School, "Subject Icon") },
                        label = {Text("Fach")},
                        state = subject,
                    )

                    FilledIconButton(
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = DataSharer.roundShape,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top= 8.dp, bottom = 4.dp),
                        onClick = {
                            openAlertDialog.value = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = "Delete subject"
                        )
                    }
                }

                Row (
                    modifier = Modifier.height(70.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        modifier = Modifier.padding(end= 16.dp).weight(1f),
                        leadingIcon = {Icon(Icons.Default.Person, "Person Icon") },
                        label = {Text("Lehrerkürzel")},
                        state = teacher,
                    )
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        leadingIcon = {Icon(Icons.Default.Room, "Room Icon") },
                        label = {Text("Raum")},
                        state = room,
                    )
                }

                // double lesson shenanigans
                val doupleLessonTexts = listOf("Einzelstunde", "Doppelstunde")
                val doupleLessonIcons = listOf(Icons.Outlined.School, Icons.Outlined.PlusOne)
                var doubleInt = 0
                if (lesson.doubleLesson) {
                    doubleInt = 1
                }
                var doupleLessonselectedItemIndex by remember { mutableIntStateOf(doubleInt) }

                ButtonGroup(
                    modifier = Modifier
                        .safeDrawingPadding()
                        .height(70.dp)
                        .padding(horizontal = 4.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    overflowIndicator = {}
                ) {
                    doupleLessonTexts.forEachIndexed { index, label ->
                        toggleableItem(
                            weight = 1f,
                            checked = doupleLessonselectedItemIndex == index,
                            onCheckedChange = { doupleLessonselectedItemIndex = index },
                            label = label,
                            icon = {
                                Icon(imageVector = doupleLessonIcons[index], contentDescription = "")
                            }
                        )
                    }
                }

                val abButtonTexts = listOf("A", "B", "AB")
                val abButtonIcons = listOf(Icons.Outlined.Today, Icons.Outlined.CalendarToday, Icons.Default.ViewWeek)
                var abSelectedItemIndex by remember { mutableIntStateOf(lesson.week.ordinal) }

                ButtonGroup(
                    modifier = Modifier
                        .safeDrawingPadding()
                        .height(70.dp)
                        .padding(horizontal = 4.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    overflowIndicator = {}
                ) {
                    abButtonTexts.forEachIndexed { index, label ->
                        toggleableItem(
                            weight = 1f,
                            checked = abSelectedItemIndex == index,
                            onCheckedChange = { abSelectedItemIndex = index },
                            label = label,
                            icon = {
                                Icon(imageVector = abButtonIcons[index], contentDescription = "")
                            }
                        )
                    }
                }

                //save button
                Button(
                    modifier = Modifier
                        .height(70.dp)
                        .padding(vertical = 10.dp)
                        .fillMaxWidth(),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            val weekType = when (abSelectedItemIndex) {
                                0 -> { WeekType.A }
                                1 -> { WeekType.B }
                                else -> { WeekType.AB }
                            }
                            val double = if (doupleLessonselectedItemIndex == 0) {
                                false
                            }else {
                                true
                            }
                            val newSubject = lesson(pos, teacher.text.toString(), subject.text.toString(), room.text.toString(), custom = true, week = weekType, doubleLesson = double, id = lesson.id)
                            var newCustomSubjects = saveSubject(newSubject, savedCustomSubjects.value, pos, day)
                            if (double) {
                                val doubleNewSubject = lesson(pos +1, teacher.text.toString(), subject.text.toString(), room.text.toString(), custom = true, week = weekType, doubleLesson = double, id = lesson.id)
                                newCustomSubjects = saveSubject(doubleNewSubject, newCustomSubjects, getCompanionLesson(pos), day)
                            }
                            scope.launch {
                                userSettings.updateCustomSubjects(newCustomSubjects)
                                println("subjects: $newCustomSubjects")
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

fun saveSubject(lesson:lesson, saved:MutableMap<Int, MutableMap<DayOfWeek, List<lesson>>>, pos: Int, day: DayOfWeek): MutableMap<Int, MutableMap<DayOfWeek, List<lesson>>> {
    val posMap = saved[pos]?.toMutableMap() ?: mutableMapOf()
    val savedSubjects = posMap[day]?.toMutableList() ?: mutableListOf()

    val index = savedSubjects.indexOfFirst { it.id == lesson.id }
    if (index != -1) {
        savedSubjects[index] = lesson
    } else {
        savedSubjects.add(lesson)
    }

    posMap[day] = savedSubjects
    val newCustomSubjects = saved.toMutableMap()
    newCustomSubjects[pos] = posMap
    return newCustomSubjects
}

fun deleteSubject(lesson:lesson, saved:MutableMap<Int, MutableMap<DayOfWeek, List<lesson>>>, pos: Int, day: DayOfWeek): MutableMap<Int, MutableMap<DayOfWeek, List<lesson>>> {
    val dayList = saved[pos]?:mutableMapOf()

    val posSubjects = dayList[day]?.toMutableList() ?: mutableListOf()
    posSubjects.removeAll { it.id == lesson.id }

    saved[pos]?.put(day, posSubjects)

    dayList[day] = posSubjects
    saved[pos] = dayList
    return saved
}

fun getCompanionLesson(pos: Int): Int {
    return if (pos <= 6) {
        if (pos % 2 == 0) {
            pos -1
        }else {
            pos + 1
        }
    }else {
        if (pos % 2 != 0) {
            pos -1
        }else {
            pos + 1
        }
    }
}