package com.capputinodevelopment.planager.Screens

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capputinodevelopment.planager.Onboarding
import com.capputinodevelopment.planager.components.LessonCard
import com.capputinodevelopment.planager.components.LessonCardCanceled
import com.capputinodevelopment.planager.components.TimestampCard
import com.capputinodevelopment.planager.data.Globals
import com.capputinodevelopment.planager.data.Globals.FilterClass
import com.capputinodevelopment.planager.data.Globals.FilterFriend
import com.capputinodevelopment.planager.data.Globals.Kurse
import com.capputinodevelopment.planager.data.Globals.doFilter
import com.capputinodevelopment.planager.data.GlobalPlan.weeks
import com.capputinodevelopment.planager.data.Globals.bottomShape
import com.capputinodevelopment.planager.data.Globals.neutralShape
import com.capputinodevelopment.planager.data.Globals.roundShape
import com.capputinodevelopment.planager.data.Globals.topShape
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.WeekType
import com.capputinodevelopment.planager.data.backend.fixDay
import com.capputinodevelopment.planager.data.backend.getKurse
import com.capputinodevelopment.planager.data.backend.getLessons
import com.capputinodevelopment.planager.data.fetchWeekType
import com.capputinodevelopment.planager.data.lesson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale


@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DayView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val userSettings = remember { UserSettings.getInstance(context.applicationContext) }
    val showTeacher by userSettings.showTeacher.collectAsState(initial = false)
    var lessons by remember { mutableStateOf<ArrayList<lesson>?>(null) }
    var ags by remember { mutableStateOf<ArrayList<lesson>?>(null) }
    val savedCustomSubjects = userSettings.customSubjects.collectAsState(mutableMapOf())
    val customColor = userSettings.customSubjectsColor.collectAsState("")
    val coroutineScope = rememberCoroutineScope()
    var current = LocalDate.now()
    val ownClass by userSettings.ownClass.collectAsState(initial = String())
    val onboarding by userSettings.onboarding.collectAsState(initial = null)
    var loading by remember { mutableStateOf<Boolean>(true) }

    val filter by remember { Globals::FilterClass }

    if (filter.isEmpty()) {
        FilterClass = ownClass
    }


    val status: State<HashMap<String, Boolean>> = if (FilterFriend == "") {
        userSettings.ownSubjects.collectAsState(initial = HashMap())
    } else {
        mutableStateOf(userSettings.friendsSubjects.collectAsState(initial = HashMap()).value.get(FilterFriend)?: HashMap())
    }

    val timeNow = LocalTime.now()
    current = fixDay(timeNow, current)

    LaunchedEffect(onboarding) {
        if (onboarding == true) {
            println("doing onboarding")
            context.startActivity(Intent(context, Onboarding::class.java))
        }
    }
    LaunchedEffect(Unit, filter) {
        if (Kurse.isEmpty()) {
            Kurse = getKurse(userSettings, current, null, context)?: ArrayList()
        }
        lessons = getLessons(userSettings, current, context= context)
        ags = getLessons(userSettings, current, "AG", context)?:arrayListOf()
        loading = false
    }
    val state = rememberPullToRefreshState()

    var isRefreshing by remember { mutableStateOf(false) }
    val onRefresh: () -> Unit = {
        val week = fetchWeekType(current)
        isRefreshing = true
        weeks[week] = mutableStateOf(
            mutableMapOf(
                DayOfWeek.MONDAY to "",
                DayOfWeek.TUESDAY to "",
                DayOfWeek.WEDNESDAY to "",
                DayOfWeek.THURSDAY to "",
                DayOfWeek.FRIDAY to ""
            )
        )
        coroutineScope.launch {lessons =
            getLessons(userSettings, current, context = context)
            isRefreshing = false
        }
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
            if (isRefreshing || loading) {
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
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            text = current.dayOfWeek.getDisplayName(
                                TextStyle.FULL_STANDALONE ,
                                Locale.GERMANY),
                        )
                    }

                    var currentLessons = lessons


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
                    } else{
                        currentLessons = currentLessons?.filter { !it.ag } as ArrayList<lesson>?
                    }
                    if (doFilter && FilterFriend.isEmpty()) {
                        //append custom subjects
                        for (pos in 0..11) {
                            val customSubjects =
                                savedCustomSubjects.value[pos]?.get(current.dayOfWeek) ?: listOf()
                            //println("cS " + customSubjects.joinToString { it.subject })
                            val filteredLessons = customSubjects.filter { lesson ->
                                lesson.week == WeekType.AB || lesson.week == fetchWeekType(current)
                            }

                            val insertIndex = currentLessons?.indexOfFirst { it.pos >= pos } ?: -1

                            if (insertIndex == -1) {
                                // Append at end if no position >= pos found
                                currentLessons?.addAll(filteredLessons)
                            } else {
                                // Insert at the found position
                                currentLessons?.addAll(insertIndex, filteredLessons)
                            }
                        }
                    }
                    currentLessons?.forEachIndexed { i, l ->
                        val topSurfaceShape = RoundedCornerShape(16.dp, 0.dp, 0.dp, 0.dp)
                        val bottomSurfaceShape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 16.dp)
                        var numberShape = roundShape
                        var shape = roundShape
                        var surfaceShape = roundShape

                        val pos = l.pos

                        if (doFilter) {
                            //TODO Clean this mess up
                            if (i + 1 <= currentLessons.size - 1) {
                                if (l.pos < 8) {
                                    if (pos % 2 == 0) {
                                        numberShape = bottomShape
                                        if (currentLessons[i + 1].pos > pos) {
                                            shape = bottomShape
                                            surfaceShape = bottomSurfaceShape
                                        }
                                    } else {
                                        numberShape = topShape
                                        shape = topShape
                                        surfaceShape = topSurfaceShape
                                    }
                                } else {
                                    if (pos % 2 != 0) {
                                        numberShape = bottomShape

                                        if (currentLessons[i + 1].pos > pos) {
                                            shape = bottomShape
                                            surfaceShape = bottomSurfaceShape
                                        }
                                    } else {
                                        numberShape = topShape
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
                            numberShape = roundShape
                        }

                        val previousPos = currentLessons.getOrNull(i - 1)?.pos
                        var visible by remember { mutableStateOf(false) }

                        LaunchedEffect(Unit) {
                            delay(i * 50L) // staggered animation
                            visible = true
                        }
                        if (previousPos != null && pos > previousPos + 1) {
                            AnimatedVisibility(
                                visible = visible,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().height(40.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    LinearWavyProgressIndicator(
                                        progress = { 1f },
                                        amplitude = { 3f },
                                        waveSpeed = 0.dp
                                    )
                                }
                            }

                        }
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            var visible by remember { mutableStateOf(false) }

                            LaunchedEffect(Unit) {
                                delay(i * 50L) // staggered animation
                                visible = true
                            }

                            // Timestamp animation
                            AnimatedVisibility(
                                visible = visible && pos > 0,
                                enter = slideInHorizontally(initialOffsetX = { -it / 2 }) + fadeIn(),
                                exit = slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut()
                            ) {
                                TimestampCard(l, numberShape)
                            }

                            Spacer(Modifier.width(4.dp))
                            // Lesson card animation
                            AnimatedVisibility(
                                visible = visible,
                                enter = slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn(),
                                exit = slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut()
                            ) {
                                //this ensures no placeholder cards and abandoned custom subjects without ids are being shown
                                if (!l.placeHolder && ((l.custom && l.id.isNotEmpty()) || !l.custom)) {
                                    if (!l.canceled) {
                                        LessonCard(l, showTeacher, shape, surfaceShape, customColor.value)
                                    } else {
                                        LessonCardCanceled(l, shape)
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
