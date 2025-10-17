package com.capputinodevelopment.planager.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingToolbarExitDirection.Companion.Bottom
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderDefaults.Track
import androidx.compose.material3.SliderPositions
import androidx.compose.material3.SliderState
import androidx.compose.material3.VerticalFloatingToolbar
import androidx.compose.material3.VerticalSlider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.sp
import com.capputinodevelopment.planager.data.DataSharer
import com.capputinodevelopment.planager.data.RobotoFlexVariable
import java.time.DayOfWeek
import kotlin.math.exp

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SliderToolBar() {
    val vibrantColors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()
    var expanded by remember { mutableStateOf(false) }
    Icons.Filled.Check
    Icons.Filled.CalendarToday
    val sliderState = rememberSliderState(
            value = getSliderFloat(DataSharer.SliderState.value),
            steps = 3,
            valueRange = 0f..5f,
    )
    sliderState.onValueChangeFinished = {
        DataSharer.SliderState.value = getSliderDayOfWeek(sliderState.value)
    }

    VerticalFloatingToolbar(
        expanded = expanded,
        floatingActionButton = {
            FloatingToolbarDefaults.VibrantFloatingActionButton(
                onClick = {
                    expanded = !expanded
                }
            ) {
                Text(fontFamily = RobotoFlexVariable, text = getSliderText(sliderState.value), fontSize = 35.sp,)
            }
        },
        colors = vibrantColors,
        content = {
            Column(modifier = Modifier.width(50.dp)) {
            DaySlider(sliderState)
            }
        },
    )
}
fun getSliderText(state: Float): String {
    return when(state) {
        0.0f -> "Mo"
        1.25f -> "Di"
        2.5f -> "Mi"
        3.75f -> "Do"
        else -> "Fr"
    }
}
fun getSliderDayOfWeek(state: Float): DayOfWeek {
    return when(state) {
        0.0f -> DayOfWeek.MONDAY
        1.25f -> DayOfWeek.TUESDAY
        2.5f -> DayOfWeek.WEDNESDAY
        3.75f -> DayOfWeek.THURSDAY
        else -> DayOfWeek.FRIDAY
    }
}

fun getSliderFloat(state: DayOfWeek): Float {
    return when(state) {
        DayOfWeek.MONDAY ->  0.0f
        DayOfWeek.TUESDAY -> 1.25f
        DayOfWeek.WEDNESDAY -> 2.5f
        DayOfWeek.THURSDAY -> 3.75f
        else -> 5.0f
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DaySlider(sliderState: SliderState) {

    VerticalSlider(

        state = sliderState,
        reverseDirection = true,
        colors = SliderColors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            activeTickColor = MaterialTheme.colorScheme.surfaceContainer,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceDim,
            inactiveTickColor = MaterialTheme.colorScheme.primary,
            disabledThumbColor = MaterialTheme.colorScheme.primary,
            disabledActiveTrackColor = MaterialTheme.colorScheme.surfaceDim,
            disabledActiveTickColor = MaterialTheme.colorScheme.surfaceDim,
            disabledInactiveTrackColor = MaterialTheme.colorScheme.surfaceDim,
            disabledInactiveTickColor = MaterialTheme.colorScheme.surfaceDim,
        ),
        modifier = Modifier.height(400.dp)
    )
}