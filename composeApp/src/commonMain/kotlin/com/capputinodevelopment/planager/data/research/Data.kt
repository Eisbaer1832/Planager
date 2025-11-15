package com.capputinodevelopment.planager.data.research

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.capputinodevelopment.planager.data.lesson
import java.time.DayOfWeek

data class Data(
    var days: MutableState<MutableMap<DayOfWeek, ArrayList<lesson>>> = mutableStateOf(
        mutableMapOf(
            DayOfWeek.MONDAY to arrayListOf(),
            DayOfWeek.TUESDAY to arrayListOf(),
            DayOfWeek.WEDNESDAY to arrayListOf(),
            DayOfWeek.THURSDAY to arrayListOf(),
            DayOfWeek.FRIDAY to arrayListOf()
        )
    )
)
