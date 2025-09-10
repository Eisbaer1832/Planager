package com.example.indiwarenative.data
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.indiwarenative.data.backend.fetchTimetable
import java.time.DayOfWeek

object GlobalPlan {
    var monday = ""
    var tuesday = ""
    var wednesday = ""
    var thursday = ""
    var friday = ""
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun getDay(day: DayOfWeek, userSettings: UserSettings, url: String): String {
    var dayMap = mutableMapOf(
        DayOfWeek.MONDAY to GlobalPlan.monday,
        DayOfWeek.TUESDAY to GlobalPlan.tuesday,
        DayOfWeek.WEDNESDAY to GlobalPlan.wednesday,
        DayOfWeek.THURSDAY to GlobalPlan.thursday,
        DayOfWeek.FRIDAY to GlobalPlan.friday
    )
    var dayXML= dayMap[day]?: String()
    if (dayXML == "") {
        dayMap[day] = fetchTimetable(userSettings, url, null)
        dayXML = dayMap[day]?: ""
    }

    return dayXML
}