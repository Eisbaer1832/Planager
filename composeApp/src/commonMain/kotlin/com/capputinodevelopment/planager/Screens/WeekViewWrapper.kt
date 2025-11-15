package com.capputinodevelopment.planager.Screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.capputinodevelopment.planager.data.WeekType
import com.capputinodevelopment.planager.data.fetchWeekType
import java.time.LocalDate
import java.time.temporal.IsoFields
import java.time.temporal.WeekFields
import java.util.Locale

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun WeekViewWrapper(modifier: Modifier, editSubjects: Boolean, updateWeekType: (weekType: WeekType) -> Unit) {
    val listState = rememberLazyListState()

    Column(modifier = modifier) {
        LazyRow(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState) // the fancy snap
        ) {
            items(2, key = { it }) { index ->
                val date = if (index == 0) LocalDate.now() else LocalDate.now().plusWeeks(1)

                LaunchedEffect(listState.firstVisibleItemIndex) {
                    val visibleIndex = listState.firstVisibleItemIndex
                    val date = if (visibleIndex == 0) LocalDate.now() else LocalDate.now().plusWeeks(1)
                    println("week of ${date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)} ${fetchWeekType(date)}")
                    updateWeekType(fetchWeekType(date))
                }

                Row(
                    Modifier
                        .animateItem(
                            fadeInSpec = tween(durationMillis = 250),
                            fadeOutSpec = tween(durationMillis = 100),
                            placementSpec = spring(
                                stiffness = Spring.StiffnessLow,
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            )
                        )
                        .fillParentMaxWidth()
                ) {
                    WeekView(Modifier, editSubjects, date)
                }
            }
        }
    }
}
