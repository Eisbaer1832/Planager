package com.capputinodevelopment.planager.data
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.capputinodevelopment.planager.data.GlobalPlan.days
import com.capputinodevelopment.planager.data.GlobalPlan.kurse
import com.capputinodevelopment.planager.data.backend.fetchTimetable
import com.capputinodevelopment.planager.data.backend.fixDay
import kotlinx.datetime.*

object GlobalPlan {
    @SuppressLint("MutableCollectionMutableState")
    var weeks: MutableMap<WeekType, MutableState<MutableMap<DayOfWeek, String>>> = mutableMapOf(
        WeekType.A to mutableStateOf(
            mutableMapOf(
                DayOfWeek.MONDAY to "",
                DayOfWeek.TUESDAY to "",
                DayOfWeek.WEDNESDAY to "",
                DayOfWeek.THURSDAY to "",
                DayOfWeek.FRIDAY to ""
            )
        ),
        WeekType.B to mutableStateOf(
            mutableMapOf(
                DayOfWeek.MONDAY to "",
                DayOfWeek.TUESDAY to "",
                DayOfWeek.WEDNESDAY to "",
                DayOfWeek.THURSDAY to "",
                DayOfWeek.FRIDAY to ""
            )
        )
    )
    var kurse by mutableStateOf("")
}



suspend fun getDayXML(day: DayOfWeek, userSettings: UserSettings): String {


    val current = fixDay( getToday())

    if (current.dayOfWeek > day) {
        generateSequence(current) { it.minus(1, DateTimeUnit.DAY) }
            .first { it.dayOfWeek == day }
    }else{
        generateSequence(current) { it.plus(1, DateTimeUnit.DAY) }
            .first { it.dayOfWeek == day }
    }

    val currentAsString = date.format(formatter)
    val week = fetchWeekType(date)

    var dayXML= weeks[week]?.value[day]?:""
    if (dayXML.isEmpty()) {
        println("Updating global Variable")
        var result = fetchTimetable(userSettings, "/mobil/mobdaten/PlanKl${currentAsString}.xml", null, context)
        weeks[week]?.value = weeks[week]?.value?.toMutableMap()?.apply { this[day] = result } ?: mutableMapOf(day to result)

        dayXML = result
    }
    println("dayData for $day: ${weeks[week]?.value[day]?.length}")
    return dayXML
}

suspend fun getKurseXML(userSettings: UserSettings, context: Context): String {
    if (kurse == "") {
        println("Updating global Variable")
        kurse = fetchTimetable(userSettings, "/mobil/mobdaten/Klassen.xml", null, context)
    }
    return kurse
}