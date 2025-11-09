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
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import java.time.LocalDate
import java.time.temporal.TemporalAmount
import java.time.temporal.TemporalUnit

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun WeekViewWrapper(modifier: Modifier, editSubjects: Boolean) {
    val listState = rememberLazyListState()

    Column(modifier = modifier) {
        LazyRow(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState) // <-- snapping
        ) {
            items(2, key = { it }) { index ->
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
                        .fillParentMaxWidth() // optional: makes each item full width
                ) {
                    if (index == 0) {
                        WeekView(Modifier, editSubjects, LocalDate.now())
                    }else {
                        WeekView(Modifier, editSubjects, LocalDate.now().plusWeeks(1))
                    }
                }
            }
        }
    }
}
