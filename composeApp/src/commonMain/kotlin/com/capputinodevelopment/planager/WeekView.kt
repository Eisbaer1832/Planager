package com.capputinodevelopment.planager

import androidx.annotation.RequiresApi
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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capputinodevelopment.planager.data.DataSharer.FilterFriend
import com.capputinodevelopment.planager.data.DataSharer.doFilter
import com.capputinodevelopment.planager.data.backend.getLessons
import com.capputinodevelopment.planager.components.TopBar
import com.capputinodevelopment.planager.data.DataSharer
import com.capputinodevelopment.planager.data.DataSharer.FilterClass
import com.capputinodevelopment.planager.data.GlobalPlan.days
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.backend.fixDay
import com.capputinodevelopment.planager.data.lesson
import com.capputinodevelopment.planager.ui.theme.IndiwareNativeTheme
import kotlinx.serialization.json.Json.Default.configuration
import kotlin.getValue



@Composable
fun SmallLessonCard (lesson: lesson) {
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



@Composable
fun WeekView(modifier: Modifier = Modifier, userSettings: UserSettings) {
    val subjectsToShow by userSettings.ownSubjects.collectAsState(initial = HashMap())
    val friendsSubjects by userSettings.friendsSubjects.collectAsState(initial = HashMap())
    var week by remember { mutableStateOf(arrayListOf<ArrayList<lesson>>()) }
    var isLoading by remember { mutableStateOf(true) }
    DateTimeFormatter.ofPattern("yyyyMMdd")
    val formatterDisplay = DateTimeFormatter.ofPattern("dd.MM.")
    var current = getToday()
    current = current.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    current = fixDay(null, current)
    var orderedWeek: HashMap<Int, ArrayList<ArrayList<lesson>>> = HashMap()
    val filter by remember { DataSharer::FilterClass }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    val ownClass by userSettings.ownClass.collectAsState(initial = String())
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
                    context = context
                )

            var today = getToday()
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
                            Column {
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
                for ( pos in 1..orderedWeek.size - 1 ) {

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
                                    Spacer(modifier = Modifier.width(configuration.screenWidthDp.dp / 6))
                                } else {
                                    Column {
                                        for (j in 0..<(orderedWeek[pos]?.get(i)?.size ?: 0)) {
                                            var show = true
                                            val lesson = orderedWeek.get(pos)?.get(i)?.get(j)?: lesson()
                                            val currentSubject = lesson.subject
                                            if (doFilter){
                                                if (currentSubject.contains(Regex("\\d")) || currentSubject.contains(Regex("-P")) || currentSubject.contains(Regex("-W")) || lesson.ag) {
                                                    println("filtering object" + currentSubject)
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
                                                Spacer(modifier = Modifier.width(configuration.screenWidthDp.dp / 6))
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



@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    IndiwareNativeTheme {
        WeekView()
    }
}