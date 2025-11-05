package com.capputinodevelopment.planager.Screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capputinodevelopment.planager.components.SubjectCreateSheet
import com.capputinodevelopment.planager.data.DataSharer.FilterFriend
import com.capputinodevelopment.planager.data.DataSharer.doFilter
import com.capputinodevelopment.planager.data.backend.getLessons
import com.capputinodevelopment.planager.data.DataSharer
import com.capputinodevelopment.planager.data.DataSharer.FilterClass
import com.capputinodevelopment.planager.data.GlobalPlan.days
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.backend.fixDay
import com.capputinodevelopment.planager.data.lesson
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import kotlin.getValue


@Composable
fun SmallLessonCard (lesson: lesson, editing: Boolean, onClick: () -> Unit,) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    Card(
        modifier = Modifier
            .width(screenWidth / 6)
            .height(80.dp)
            .padding(3.dp)
            .clickable {
                if (editing) {
                    println("launching edit dialog")
                    onClick()
                }
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var primaryColor = MaterialTheme.colorScheme.primaryContainer
            if (lesson.custom) {
                primaryColor = MaterialTheme.colorScheme.tertiaryContainer
            }
            Surface  (
                color = primaryColor,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = lesson.subject
                )
            }
            if (editing && lesson.custom) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment= Alignment.Center
                ) {
                    Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(35.dp))
                }
            }else {
                Text(
                    text = lesson.teacher.replace("\n", "")
                )
                val roomColor =  if (lesson.roomChanged) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary

                Text(
                    text = lesson.room,
                    color = roomColor

                )
            }
        }
    }
}


@Composable
fun SmallLessonCardCanceled (lesson: lesson) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    var text = lesson.subject
    text = text
        .replace(Regex("fällt aus"), "")
        .replace(Regex("Herr"), "")
        .replace(Regex("Frau"), "")
    val textArray = text.split("  ") //yes actually 2 spaces

    Card(
        modifier = Modifier
            .width(screenWidth / 6)
            .padding(3.dp)
            .height(70.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.error,
            ) {
                Text(
                    fontSize = 19.sp,
                    style = TextStyle(textDecoration = TextDecoration.LineThrough),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = textArray[0]
                )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = textArray[1]
            )
        }
    }

}



@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun WeekView(modifier: Modifier = Modifier, editLocalSubjects: Boolean) {
    val context = LocalContext.current
    val userSettings = UserSettings.getInstance(context.applicationContext)
    val subjectsToShow by userSettings.ownSubjects.collectAsState(initial = HashMap())
    val friendsSubjects by userSettings.friendsSubjects.collectAsState(initial = HashMap())
    var week by remember { mutableStateOf(arrayListOf<ArrayList<lesson>>()) }
    var isLoading by remember { mutableStateOf(true) }
    val formatterDisplay = DateTimeFormatter.ofPattern("dd.MM.")
    var current = LocalDate.now()
    current = current.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    current = fixDay(null, current)
    var orderedWeek by remember { mutableStateOf(HashMap<Int, ArrayList<ArrayList<lesson>>>())}
    val filter by remember { DataSharer::FilterClass }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    val ownClass by userSettings.ownClass.collectAsState(initial = String())
    var weekDates by remember { mutableStateOf(arrayListOf<LocalDate>())}
    var createSubjectWeekDay by remember { mutableStateOf(DayOfWeek.MONDAY) }
    var createSubjectLesson by remember { mutableStateOf(lesson()) }
    val showCreateSubjectSheet = remember { mutableStateOf(false) }
    val savedCustomSubjects = userSettings.customSubjects.collectAsState(mutableMapOf())

    if (filter.isEmpty()) {
        FilterClass = ownClass
    }
    if (showCreateSubjectSheet.value) {
        SubjectCreateSheet(showCreateSubjectSheet, userSettings, createSubjectWeekDay,createSubjectLesson)
    }

    LaunchedEffect(Unit, filter, refreshTrigger) {

        // loading a full school week
        isLoading = true
        week = arrayListOf<ArrayList<lesson>>()
        weekDates = ArrayList<LocalDate>()
        for (i in 0..4) {
            println("cdom: "+ current.dayOfMonth)
            val lesson = getLessons(userSettings,current.dayOfWeek,context = context)

            var today = LocalDate.now()
            today = fixDay(null, today)
            if (today.dayOfWeek > current.dayOfWeek) {
                weekDates.add( today.with(TemporalAdjusters.previousOrSame(current.dayOfWeek)))
            }else{
                weekDates.add(today.with(TemporalAdjusters.nextOrSame(current.dayOfWeek)))
            }


            if (lesson != null) {
                week.add(lesson)
                current = current.plusDays(1)
            }


        }
        isLoading = false
        orderedWeek = orderWeek(week)
    }


    val state = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val onRefresh: () -> Unit = {
        days = mutableStateOf(
            mutableMapOf(
                DayOfWeek.MONDAY to "",
                DayOfWeek.TUESDAY to "",
                DayOfWeek.WEDNESDAY to "",
                DayOfWeek.THURSDAY to "",
                DayOfWeek.FRIDAY to ""
            )
        )
        refreshTrigger++ // this is a bit dumm, since it takes up memory space - should probaply reimplemented in the future #TODO
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                state = state
            )
        },
    ) {
        if (isLoading) {
            Row(
                Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
            )
            {
                LoadingIndicator(modifier = Modifier.size(60.dp))
            }
        } else {
            val days = arrayOf("Mo.", "Di.", "Mi.", "Do.", "Fr.")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Row {
                    Spacer(modifier = Modifier.width(screenWidth / 6))
                    for (i in 0..<days.size) {
                        Card(
                            modifier = Modifier
                                .width(screenWidth / 6)
                                .padding(3.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = days[i],
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top=5.dp, start = 5.dp, end = 10.dp)
                                )
                                val dayofWeek = if (!weekDates.isEmpty()) weekDates[i].format(formatterDisplay) else ""
                                Text(
                                    modifier = Modifier.padding(bottom=5.dp, start = 5.dp, end = 0.dp),

                                    text = dayofWeek,
                                )
                            }
                        }
                    }
                }
                // this loop draws the week by position then day of week
                for ( pos in 1..11 ) {
                    Row {
                        Card(
                            modifier = Modifier
                                .width(screenWidth / 6)
                                .height(80.dp)
                                .padding(10.dp, 3.dp),
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pos.toString(),
                                )
                            }
                        }
                        if (pos == 7) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .padding(3.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Mittagspause \uD83C\uDF89",
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            // this loop represents days:
                            for (i in 0..<(orderedWeek[pos]?.size ?: 0)) {
                                val customSubject = savedCustomSubjects.value[pos]?.get(DayOfWeek.of(i +1))

                                if (orderedWeek[pos]?.get(i)?.isEmpty() == true) {
                                    Spacer(modifier = Modifier.width(configuration.screenWidthDp.dp / 6))
                                } else {
                                    Column {

                                        var totalSubjects = orderedWeek[pos]?.get(i)?:listOf()

                                        // only append custom subject if it doesnt return the default error lesson
                                        if (customSubject != lesson() && customSubject != null) {
                                            totalSubjects = totalSubjects.plus(customSubject)
                                        }
                                        var displayEditButton = true

                                        for (j in 0..< totalSubjects.size) {
                                            var show = true
                                            val lesson = totalSubjects[j] ?: lesson()
                                            val currentSubject = lesson.subject
                                            if (doFilter) {
                                                if (currentSubject.contains(Regex("\\d")) || currentSubject.contains(
                                                        Regex("-P")
                                                    ) || currentSubject.contains(Regex("-W")) || lesson.ag
                                                ) {
                                                    if (FilterFriend == "") {
                                                        show =
                                                            subjectsToShow[currentSubject.substringBefore(
                                                                " "
                                                            )] == true
                                                    } else {
                                                        show = friendsSubjects.get(FilterFriend)
                                                            ?.get(currentSubject.substringBefore(" ")) == true
                                                    }
                                                }

                                            } else {
                                                if (totalSubjects[j].ag) {
                                                    show = false
                                                }
                                            }

                                            var visible by remember { mutableStateOf(true) }
                                            LaunchedEffect(Unit) {
                                                delay(i * 50L)
                                                visible = true
                                            }
                                            AnimatedVisibility(
                                                visible = visible,
                                                enter = slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn(),
                                                exit = slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut()
                                            ) {
                                                val subject = totalSubjects[j]?: lesson()
                                                if (show) {
                                                    displayEditButton = false
                                                    if (subject.canceled) {
                                                        SmallLessonCardCanceled(
                                                            subject
                                                        )
                                                    } else {
                                                        SmallLessonCard(subject, editLocalSubjects) {
                                                            createSubjectLesson = subject
                                                            createSubjectWeekDay =
                                                                DayOfWeek.of(i + 1)
                                                            showCreateSubjectSheet.value = true
                                                        }
                                                    }
                                                } else {
                                                    if (!editLocalSubjects) {
                                                        Spacer(
                                                            modifier = Modifier.width(
                                                                configuration.screenWidthDp.dp / 6
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        if (editLocalSubjects && displayEditButton) {
                                            TextButton( // a bit ironic but is perfect for this use case
                                                modifier = Modifier
                                                    .width(screenWidth / 6)
                                                    .height(80.dp),
                                                onClick = {
                                                    createSubjectLesson = lesson(pos, "", "", "")
                                                    createSubjectWeekDay = DayOfWeek.of(i + 1)
                                                    showCreateSubjectSheet.value = true
                                                }
                                            ) { Icon(Icons.Default.Add, "Fach hinzufügen") }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

}
fun orderWeek(
    week: ArrayList<ArrayList<lesson>>,
    minPos: Int = 1,
    maxPos: Int = 12
): HashMap<Int, ArrayList<ArrayList<lesson>>> {

    val newWeek = HashMap<Int, ArrayList<ArrayList<lesson>>>()
    for (p in minPos..maxPos) newWeek[p] = arrayListOf()

    for (i in 0 until week.size) {

        val day = ArrayList(week[i])

        var j = 0
        for (p in minPos..maxPos) {
            val group = arrayListOf<lesson>()
            while (j < day.size && day[j].pos == p) {
                group.add(day[j])
                j++
            }
            //check for empty lessons
            newWeek[p]!!.add(group)
        }
    }

    return newWeek
}


