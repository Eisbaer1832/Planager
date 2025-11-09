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
import com.capputinodevelopment.planager.data.research.ResearchWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import kotlin.collections.set

object GlobalPlan {
    @SuppressLint("MutableCollectionMutableState")
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



suspend fun getDayXML(datePassed: LocalDate, userSettings: UserSettings, context: Context): String {

    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    var date = fixDay(null, datePassed)
    val day = date.dayOfWeek

    if (date.dayOfWeek > day) {
        date = date.with(TemporalAdjusters.previousOrSame(day))
    }else{
        date = date.with(TemporalAdjusters.nextOrSame(day))
    }

    val currentAsString = date.format(formatter)


    var dayXML= days.value[day]?:""
    if (dayXML.isEmpty()) {
        println("Updating global Variable")
        var result = fetchTimetable(userSettings, "/mobil/mobdaten/PlanKl${currentAsString}.xml", null, context)
        days.value = days.value.toMutableMap().apply {this[day] = result}

        dayXML = result
    }
    println("dayData for $day: ${days.value[day]?.length}")
    return dayXML
}

suspend fun getKurseXML(userSettings: UserSettings, context: Context): String {
    if (kurse == "") {
        println("Updating global Variable")
        kurse = fetchTimetable(userSettings, "/mobil/mobdaten/Klassen.xml", null, context)
    }
    return kurse
}