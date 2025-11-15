package com.capputinodevelopment.planager.Screens

<<<<<<<< HEAD:composeApp/src/commonMain/kotlin/com/capputinodevelopment/planager/Screens/WeekView.kt
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
========
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
>>>>>>>> origin/kmp:composeApp/src/commonMain/kotlin/com/capputinodevelopment/planager/WeekView.kt
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
<<<<<<<< HEAD:composeApp/src/commonMain/kotlin/com/capputinodevelopment/planager/Screens/WeekView.kt
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.capputinodevelopment.planager.components.SmallLessonCard
import com.capputinodevelopment.planager.components.SmallLessonCardCanceled
import com.capputinodevelopment.planager.components.SubjectCreateSheet
import com.capputinodevelopment.planager.data.DataSharer.FilterFriend
import com.capputinodevelopment.planager.data.DataSharer.doFilter
import com.capputinodevelopment.planager.data.backend.getLessons
import com.capputinodevelopment.planager.data.DataSharer
import com.capputinodevelopment.planager.data.DataSharer.FilterClass
import com.capputinodevelopment.planager.data.GlobalPlan.weeks
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.backend.fixDay
import com.capputinodevelopment.planager.data.fetchWeekType
import com.capputinodevelopment.planager.data.lesson
import com.capputinodevelopment.planager.data.WeekType
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import kotlin.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun WeekView(modifier: Modifier = Modifier, editLocalSubjects: Boolean, datePassed: LocalDate) {
    val context = LocalContext.current
    val userSettings = UserSettings.getInstance(context.applicationContext)
========
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capputinodevelopment.planager.data.DataSharer
import com.capputinodevelopment.planager.data.DataSharer.FilterClass
import com.capputinodevelopment.planager.data.DataSharer.FilterFriend
import com.capputinodevelopment.planager.data.DataSharer.doFilter
import com.capputinodevelopment.planager.data.GlobalPlan.days
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.backend.fixDay
import com.capputinodevelopment.planager.data.backend.getLessons
import com.capputinodevelopment.planager.data.getToday
import com.capputinodevelopment.planager.data.lesson
import com.capputinodevelopment.planager.data.nextOrSame
import com.capputinodevelopment.planager.data.previousOrSame
import com.capputinodevelopment.planager.data.previousOrSameMonday
import com.capputinodevelopment.planager.ui.theme.IndiwareNativeTheme
import io.ktor.client.request.invoke
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.plus


@Composable
fun SmallLessonCard (lesson: lesson) {
    val configuration = LocalWindowInfo.current.containerSize
    val screenWidth = configuration.width.dp

    Card(
        modifier = Modifier
            .width(screenWidth / 6)
            .padding(3.dp)


    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Surface  (
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = lesson.subject
                )
            }
            Text(
                text = lesson.teacher
            )
            val roomColor =  if (lesson.roomChanged) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary

            Text(
                text = lesson.room,
                color = roomColor

            )
        }
    }
}


@Composable
fun SmallLessonCardCanceled (lesson: lesson) {
    val configuration = LocalWindowInfo.current.containerSize
    val screenWidth = configuration.width.dp

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



@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeekView(modifier: Modifier = Modifier, userSettings: UserSettings) {
>>>>>>>> origin/kmp:composeApp/src/commonMain/kotlin/com/capputinodevelopment/planager/WeekView.kt
    val subjectsToShow by userSettings.ownSubjects.collectAsState(initial = HashMap())
    val friendsSubjects by userSettings.friendsSubjects.collectAsState(initial = HashMap())
    var week by remember { mutableStateOf(arrayListOf<ArrayList<lesson>>()) }
    var isLoading by remember { mutableStateOf(true) }
<<<<<<<< HEAD:composeApp/src/commonMain/kotlin/com/capputinodevelopment/planager/Screens/WeekView.kt
    val formatterDisplay = DateTimeFormatter.ofPattern("dd.MM.")
    var date = datePassed.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    date = fixDay(null, date)
    var orderedWeek by remember { mutableStateOf(HashMap<Int, ArrayList<ArrayList<lesson>>>())}
    val filter by remember { DataSharer::FilterClass }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    val ownClass by userSettings.ownClass.collectAsState(initial = String())
    var weekDates by remember { mutableStateOf(arrayListOf<LocalDate>())}
    var createSubjectWeekDay by remember { mutableStateOf(DayOfWeek.MONDAY) }
    var createSubjectLesson by remember { mutableStateOf(lesson()) }
    val showCreateSubjectSheet = remember { mutableStateOf(false) }
    val savedCustomSubjects = userSettings.customSubjects.collectAsState(mutableMapOf())
    val customColor = userSettings.customSubjectsColor.collectAsState("")

========

    var current = fixDay(getToday())
    current = previousOrSameMonday(current)

    var orderedWeek: HashMap<Int, ArrayList<ArrayList<lesson>>> = HashMap()
    val filter by remember { DataSharer::FilterClass }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    val ownClass by userSettings.ownClass.collectAsState(initial = "")
    var weekDates = ArrayList<LocalDate>()
>>>>>>>> origin/kmp:composeApp/src/commonMain/kotlin/com/capputinodevelopment/planager/WeekView.kt
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
<<<<<<<< HEAD:composeApp/src/commonMain/kotlin/com/capputinodevelopment/planager/Screens/WeekView.kt
            println("cdom: "+ date.dayOfMonth)
            val lesson = getLessons(userSettings,date,context = context)

            var today = date
            today = fixDay(null, today)
            if (today.dayOfWeek > date.dayOfWeek) {
                weekDates.add( today.with(TemporalAdjusters.previousOrSame(date.dayOfWeek)))
            }else{
                weekDates.add(today.with(TemporalAdjusters.nextOrSame(date.dayOfWeek)))
========
            println("cdom: "+ current.day)
            val lesson =
                getLessons(
                    userSettings,
                    current.dayOfWeek,
                )

            var today = fixDay(getToday())
            if (today.dayOfWeek > current.dayOfWeek) {
                weekDates.add(previousOrSame(today, current.dayOfWeek))
            }else{
                weekDates.add(nextOrSame(today, current.dayOfWeek))
>>>>>>>> origin/kmp:composeApp/src/commonMain/kotlin/com/capputinodevelopment/planager/WeekView.kt
            }


            if (lesson != null) {
                week.add(lesson)
<<<<<<<< HEAD:composeApp/src/commonMain/kotlin/com/capputinodevelopment/planager/Screens/WeekView.kt
                date = date.plusDays(1)
========
                current = current.plus(1, DateTimeUnit.DAY)
>>>>>>>> origin/kmp:composeApp/src/commonMain/kotlin/com/capputinodevelopment/planager/WeekView.kt
            }


        }
        val newOrderedWeek = withContext(Dispatchers.Default) {
            orderWeek(week)
        }
        orderedWeek = newOrderedWeek
        isLoading = false
    }


    val state = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val onRefresh: () -> Unit = {
        val week = fetchWeekType(date)
        weeks[week] = mutableStateOf(
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

    val configuration = LocalWindowInfo.current.containerSize
    val screenWidth = configuration.width.dp

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
                                val dayofWeek = if (!weekDates.isEmpty()) weekDates[i].day else ""
                                Text(
                                    modifier = Modifier.padding(bottom=5.dp, start = 5.dp, end = 0.dp),
                                    text = dayofWeek.toString(),
                                )
                            }
                        }
                    }
                }
<<<<<<<< HEAD:composeApp/src/commonMain/kotlin/com/capputinodevelopment/planager/Screens/WeekView.kt
                // this loop draws the week by position then day of week
                for ( pos in 1..11 ) {
========
                for ( pos in 1..<orderedWeek.size) {

>>>>>>>> origin/kmp:composeApp/src/commonMain/kotlin/com/capputinodevelopment/planager/WeekView.kt
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
                                val customSubjects = savedCustomSubjects.value[pos]?.get(DayOfWeek.of(i +1))?:listOf()
                                if (orderedWeek[pos]?.get(i)?.isEmpty() == true) {
                                    Spacer(modifier = Modifier.width(screenWidth / 6))
                                } else {
                                    Column (
                                        Modifier.animateContentSize()
                                    ) {

                                        var totalSubjects = orderedWeek[pos]?.get(i)?:listOf()

                                        if (doFilter && FilterFriend == "") {
                                            for (customSubject in customSubjects) {
                                                // only append custom subject if it doesnt return the default error lesson
                                                if (customSubject != lesson()) {
                                                    if (customSubject.week == WeekType.AB || customSubject.week == fetchWeekType(
                                                            date
                                                        )
                                                    ) {
                                                        totalSubjects = totalSubjects.plus(customSubject)
                                                    }
                                                }
                                            }
                                        }
                                        var displayEditButton = true

                                        for (j in 0..< totalSubjects.size) {
                                            var show = true
<<<<<<<< HEAD:composeApp/src/commonMain/kotlin/com/capputinodevelopment/planager/Screens/WeekView.kt
                                            val lesson = totalSubjects[j]
                                            val currentSubject = lesson.subject
                                            if (doFilter && !lesson.custom) {
                                                if (currentSubject.contains(Regex("\\d")) || currentSubject.contains(
                                                        Regex("-P")
                                                    ) || currentSubject.contains(Regex("-W")) || lesson.ag
                                                ) {
                                                    show = if (FilterFriend == "") {
                                                        subjectsToShow[currentSubject.substringBefore(" ")] == true
                                                    } else {
                                                        friendsSubjects[FilterFriend]?.get(currentSubject.substringBefore(" ")) == true
========
                                            val lesson = orderedWeek[pos]?.get(i)?.get(j)?: lesson()
                                            val currentSubject = lesson.subject
                                            if (doFilter){
                                                if (currentSubject.contains(Regex("\\d")) || currentSubject.contains(Regex("-P")) || currentSubject.contains(Regex("-W")) || lesson.ag) {
                                                    println("filtering object$currentSubject")
                                                    if (FilterFriend == "") {
                                                        show = subjectsToShow[currentSubject.substringBefore(" ")] == true
                                                    }else {
                                                        show = friendsSubjects.get(FilterFriend)?.get(currentSubject.substringBefore(" ")) == true
>>>>>>>> origin/kmp:composeApp/src/commonMain/kotlin/com/capputinodevelopment/planager/WeekView.kt
                                                    }
                                                }

                                            } else {
                                                if (totalSubjects[j].ag) {
                                                    show = false
                                                }
                                            }

                                            var visible by remember { mutableStateOf(true) }
                                            LaunchedEffect(Unit) {

<<<<<<<< HEAD:composeApp/src/commonMain/kotlin/com/capputinodevelopment/planager/Screens/WeekView.kt
                                                visible = true
========
                                            if (show){
                                                val subject = orderedWeek.get(pos)?.get(i)?.get(j) ?: lesson()
                                                if (subject.canceled) {
                                                    SmallLessonCardCanceled(
                                                        subject
                                                    )
                                                }else {
                                                    SmallLessonCard(
                                                        subject
                                                    )
                                                }
                                            } else {
                                                Spacer(modifier = Modifier.width(screenWidth / 6))
>>>>>>>> origin/kmp:composeApp/src/commonMain/kotlin/com/capputinodevelopment/planager/WeekView.kt
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
                                                        SmallLessonCard(subject, editLocalSubjects, customColor.value) {
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


