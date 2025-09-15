package com.example.indiwarenative.data
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.indiwarenative.data.GlobalPlan.days
import com.example.indiwarenative.data.GlobalPlan.kurse
import com.example.indiwarenative.data.backend.fetchTimetable
import com.example.indiwarenative.data.backend.fixDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import kotlin.collections.set

object GlobalPlan {
    @SuppressLint("MutableCollectionMutableState")
    @RequiresApi(Build.VERSION_CODES.O)
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



@RequiresApi(Build.VERSION_CODES.O)
suspend fun getDayXML(day: DayOfWeek, userSettings: UserSettings): String {


    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    var current = LocalDate.now()
    current = fixDay(null, current)
    current = current.with(TemporalAdjusters.nextOrSame(day))
    val currentAsString = current.format(formatter)


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

@RequiresApi(Build.VERSION_CODES.O)
suspend fun getKurseXML(userSettings: UserSettings): String {
    if (kurse == "") {
        println("Updating global Variable")
        kurse = fetchTimetable(userSettings, "/mobil/mobdaten/Klassen.xml", null)
    }
    return kurse
}