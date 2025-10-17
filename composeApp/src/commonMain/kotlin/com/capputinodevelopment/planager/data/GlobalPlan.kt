package com.capputinodevelopment.planager.data
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.capputinodevelopment.planager.data.GlobalPlan.days
import com.capputinodevelopment.planager.data.GlobalPlan.kurse
import com.capputinodevelopment.planager.data.backend.fetchTimetable
import com.capputinodevelopment.planager.data.backend.fixDay
import kotlinx.datetime.*

object GlobalPlan {
    var days = mutableStateOf(
        mutableMapOf(
            DayOfWeek.MONDAY to "",
            DayOfWeek.TUESDAY to "",
            DayOfWeek.WEDNESDAY to "",
            DayOfWeek.THURSDAY to "",
            DayOfWeek.FRIDAY to ""
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

    val currentAsString = current.format(LocalDate.Format {
        current.day.toString() + current.month + current.year
    })


    var dayXML= days.value[day]?:""
    if (dayXML.isEmpty()) {
        println("Updating global Variable")
        val result = fetchTimetable(userSettings, "/mobil/mobdaten/PlanKl${currentAsString}.xml", null)
        days.value = days.value.toMutableMap().apply {this[day] = result}

        dayXML = result
    }
    println("dayData for $day: ${days.value[day]?.length}")
    return dayXML
}

suspend fun getKurseXML(userSettings: UserSettings): String {
    if (kurse == "") {
        println("Updating global Variable")
        kurse = fetchTimetable(userSettings, "/mobil/mobdaten/Klassen.xml", null)
    }
    return kurse
}