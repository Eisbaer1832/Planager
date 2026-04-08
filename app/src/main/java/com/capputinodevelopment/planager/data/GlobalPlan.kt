package com.capputinodevelopment.planager.data
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.capputinodevelopment.planager.data.GlobalPlan.kurse
import com.capputinodevelopment.planager.data.GlobalPlan.weeks
import com.capputinodevelopment.planager.data.backend.fetchTimetable
import com.capputinodevelopment.planager.data.backend.fixDay
import com.capputinodevelopment.planager.data.backend.getDayFromSearchServer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import kotlin.collections.mutableMapOf
import kotlin.collections.set

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



suspend fun getDayXML(
    datePassed: LocalDate,
    userSettings: UserSettings,
    context: Context,
    useSearchServer: Boolean = false,
    ignoreCache: Boolean = false
): String {

    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    var date = fixDay(null, datePassed)
    val day = date.dayOfWeek

    if (date.dayOfWeek > day) {
        date = date.with(TemporalAdjusters.previousOrSame(day))
    }else{
        date = date.with(TemporalAdjusters.nextOrSame(day))
    }

    val currentAsString = date.format(formatter)
    val week = fetchWeekType(date)

    var dayXML= if(!ignoreCache) weeks[week]?.value[day]?:"" else null
    if (dayXML.isNullOrEmpty()) {
        println("Updating global Variable")

        val result = if (!useSearchServer) {
            fetchTimetable(userSettings, "/mobil/mobdaten/PlanKl${currentAsString}.xml", null, context, ignoreCache)
        }else{
            println("search server")
            getDayFromSearchServer( currentAsString, userSettings)
        }
        weeks[week]?.value =
            weeks[week]?.value?.toMutableMap()?.apply {
                this[day] = result
            } ?: mutableMapOf(day to result)

        dayXML = result
    }
    println("dayData for $day: ${weeks[week]?.value[day]?.length}")
    return dayXML
}

suspend fun getKurseXML(userSettings: UserSettings, context: Context): String {
    if (kurse == "") {
        println("Updating global Variable")
        kurse = fetchTimetable(userSettings, "/mobil/mobdaten/Klassen.xml", lContext = context,)
    }
    return kurse
}