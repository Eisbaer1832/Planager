package com.capputinodevelopment.planager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capputinodevelopment.planager.components.LessonCard
import com.capputinodevelopment.planager.components.LessonCardCanceled
import com.capputinodevelopment.planager.components.TimestampCard
import com.capputinodevelopment.planager.data.DataSharer
import com.capputinodevelopment.planager.data.DataSharer.FilterClass
import com.capputinodevelopment.planager.data.DataSharer.FilterFriend
import com.capputinodevelopment.planager.data.DataSharer.Kurse
import com.capputinodevelopment.planager.data.DataSharer.doFilter
import com.capputinodevelopment.planager.data.GlobalPlan.days
import com.capputinodevelopment.planager.data.RobotoFlexVariable
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.backend.fixDay
import com.capputinodevelopment.planager.data.backend.getKurse
import com.capputinodevelopment.planager.data.backend.getLessons
import com.capputinodevelopment.planager.data.getToday
import com.capputinodevelopment.planager.data.lesson
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, userSettings: UserSettings) {

    val showTeacher by userSettings.showTeacher.collectAsState(initial = false)
    var lessons by remember { mutableStateOf<ArrayList<lesson>?>(null) }
    var ags by remember { mutableStateOf<ArrayList<lesson>?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val current = fixDay(getToday())
    val ownClass by userSettings.ownClass.collectAsState(initial = "")
    val onboarding by userSettings.onboarding.collectAsState(initial = null)
    var loading by remember { mutableStateOf<Boolean>(true) }

    println("starting main activity")

    val filter by remember { DataSharer::FilterClass }

    if (filter.isEmpty()) {
        FilterClass = ownClass
    }


    val status = if (FilterFriend == "") {
        userSettings.ownSubjects.collectAsState(initial = HashMap())
    } else {
        mutableStateOf(userSettings.friendsSubjects.collectAsState(initial = HashMap()).value[FilterFriend] ?: HashMap())
    }

    println("current" + current.dayOfWeek)

    LaunchedEffect(onboarding) {
        if (onboarding == true) {
            println("doing onboarding")
            //TODO Reimplement Onboarding
            //context.startActivity(Intent(context, Onboarding::class.java))
        }
    }
    LaunchedEffect(Unit, filter) {
        loading = true
        if (Kurse.isEmpty()) {
            Kurse = getKurse(userSettings, current.dayOfWeek, null)?: ArrayList()
        }
        println("getting DayData for " + current.dayOfWeek)
        lessons = getLessons(userSettings, current.dayOfWeek)
        ags = getLessons(userSettings, current.dayOfWeek, "AG")?:arrayListOf()
        loading = false
    }
    val state = rememberPullToRefreshState()
    var isRefreshing = false
    val onRefresh: () -> Unit = {
        isRefreshing = true
        days = mutableStateOf(
            mutableMapOf(
                DayOfWeek.MONDAY to "",
                DayOfWeek.TUESDAY to "",
                DayOfWeek.WEDNESDAY to "",
                DayOfWeek.THURSDAY to "",
                DayOfWeek.FRIDAY to ""
            )
        )
        coroutineScope.launch {lessons =
            getLessons(userSettings, current.dayOfWeek)
        }
        isRefreshing = false
    }

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

        Box {
            if (loading || isRefreshing) {
                Row(
                    Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                )
                {
                    LoadingIndicator(modifier = Modifier.size(60.dp))
                }
            } else {
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                )
                {

                    Row(
                        modifier = Modifier
                            .padding(vertical = 30.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            fontSize = 40.sp,
                            fontFamily = RobotoFlexVariable,
                            text = current.dayOfWeek.name,
                        )
                    }

                    var currentLessons = lessons
                    var lastPos = 0

                    if (doFilter && !FilterClass.isEmpty()) {
                        currentLessons = currentLessons
                            ?.filter { lesson ->
                                val key = lesson.subject.substringBefore(" ")
                                status.value[key] == true || (
                                        !lesson.subject.contains(Regex("\\d")) &&
                                                !lesson.subject.contains(Regex("-P")) &&
                                                !lesson.subject.contains(Regex("-W")) &&
                                                !lesson.ag
                                        )
                            }
                            ?.toCollection(ArrayList())

                        // show subject if its not filtered or it doesnt contain in number since that would be a mandatory class subject (hopefully)
                    }else{
                        currentLessons = currentLessons?.filter { !it.ag } as ArrayList<lesson>?
                    }
                    currentLessons?.forEachIndexed { i, l ->
                        val topShape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)
                        val bottomShape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp)
                        val topSurfaceShape = RoundedCornerShape(16.dp, 0.dp, 0.dp, 0.dp)
                        val bottomSurfaceShape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 16.dp)
                        val neutralShape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp)
                        val rounded = RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp)
                        var numberShape = neutralShape
                        var shape = neutralShape
                        var surfaceShape = neutralShape

                        val pos = l.pos


                        //TODO Clean this mess up
                        if (i + 1 <= currentLessons.size - 1) {
                            if (l.pos < 8) {
                                if (pos % 2 == 0) {
                                    numberShape = bottomShape
                                    if (currentLessons[i + 1].pos > pos) {
                                        shape = bottomShape
                                        surfaceShape = bottomSurfaceShape
                                    }
                                }else {
                                    numberShape = topShape
                                    if (pos > lastPos) {
                                        shape = topShape
                                        surfaceShape = topSurfaceShape
                                    }
                                }
                            }else {
                                if (pos % 2 != 0){
                                    numberShape = bottomShape

                                    if (currentLessons[i + 1].pos > pos) {
                                        shape = bottomShape
                                        surfaceShape = bottomSurfaceShape
                                    }
                                }else {
                                    numberShape = topShape
                                    if (pos > lastPos) {
                                        shape = topShape
                                        surfaceShape = topSurfaceShape
                                    }
                                }

                            }
                        }

                        if (i == currentLessons.size - 1) {
                            shape = bottomShape
                            numberShape = bottomShape
                            surfaceShape = bottomSurfaceShape
                        }

                        if (!doFilter || showTeacher) {
                            numberShape = rounded
                        }

                        if (showTeacher) {
                            surfaceShape = RoundedCornerShape(16.dp, 0.dp, 0.dp, 0.dp)
                        }

                        Row{
                            if (pos > lastPos) {
                                lastPos = pos
                                TimestampCard(l, numberShape)
                            } else {
                                Spacer(Modifier.width(90.dp))
                            }

                            if (!l.canceled) {
                                LessonCard(l, showTeacher, shape, surfaceShape)
                            } else {
                                LessonCardCanceled(l, shape)
                            }
                        }
                    }
                }
            }}
    }
}
