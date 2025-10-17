package com.capputinodevelopment.planager

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val subjectsToShow by userSettings.ownSubjects.collectAsState(initial = HashMap())
    val friendsSubjects by userSettings.friendsSubjects.collectAsState(initial = HashMap())
    var week by remember { mutableStateOf(arrayListOf<ArrayList<lesson>>()) }
    var isLoading by remember { mutableStateOf(true) }

    var current = fixDay(getToday())
    current = previousOrSameMonday(current)

    var orderedWeek: HashMap<Int, ArrayList<ArrayList<lesson>>> = HashMap()
    val filter by remember { DataSharer::FilterClass }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    val ownClass by userSettings.ownClass.collectAsState(initial = "")
    var weekDates = ArrayList<LocalDate>()
    if (filter.isEmpty()) {
        FilterClass = ownClass
    }



    LaunchedEffect(Unit, filter, refreshTrigger) {
        // loading a full school week
        isLoading = true
        week = arrayListOf<ArrayList<lesson>>()
        weekDates = ArrayList<LocalDate>()
        for (i in 0..4) {
            println("cdom: "+ current.dayOfMonth)
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
            }


            if (lesson != null) {
                week.add(lesson)
                current = current.plus(1, DateTimeUnit.DAY)
            }


        }
        isLoading = false
        orderedWeek = orderWeek(week)
    }


    val state = rememberPullToRefreshState()
    val isRefreshing = false
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
                            Column {
                                Text(
                                    text = days[i],
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top=5.dp, start = 5.dp, end = 10.dp)
                                )
                                val dayofWeek = if (!weekDates.isEmpty()) weekDates[i].dayOfWeek.name else ""
                                Text(
                                    modifier = Modifier.padding(bottom=5.dp, start = 5.dp, end = 0.dp),

                                    text = dayofWeek,
                                )
                            }
                        }
                    }
                }
                for ( pos in 1..<orderedWeek.size) {

                    Row {
                        Card(
                            modifier = Modifier
                                .width(screenWidth / 6)
                                .height(80.dp)
                                .padding( 10.dp, 3.dp),
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

                            for (i in 0..<(orderedWeek[pos]?.size ?: 0)) {
                                if (orderedWeek[pos]?.get(i)?.isEmpty() == true) {
                                    Spacer(modifier = Modifier.width(screenWidth / 6))
                                } else {
                                    Column {
                                        for (j in 0..<(orderedWeek[pos]?.get(i)?.size ?: 0)) {
                                            var show = true
                                            val lesson = orderedWeek[pos]?.get(i)?.get(j)?: lesson()
                                            val currentSubject = lesson.subject
                                            if (doFilter){
                                                if (currentSubject.contains(Regex("\\d")) || currentSubject.contains(Regex("-P")) || currentSubject.contains(Regex("-W")) || lesson.ag) {
                                                    println("filtering object$currentSubject")
                                                    if (FilterFriend == "") {
                                                        show = subjectsToShow[currentSubject.substringBefore(" ")] == true
                                                    }else {
                                                        show = friendsSubjects.get(FilterFriend)?.get(currentSubject.substringBefore(" ")) == true
                                                    }
                                                }

                                            }else {
                                                if (orderedWeek.get(pos)?.get(i)?.get(j)?.ag?:false){
                                                    show = false
                                                }
                                            }


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


