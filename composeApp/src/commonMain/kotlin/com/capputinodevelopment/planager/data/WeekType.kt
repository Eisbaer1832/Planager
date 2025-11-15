package com.capputinodevelopment.planager.data

import java.time.LocalDate
import java.time.temporal.IsoFields


enum class WeekType {
    A, B, AB
}

fun fetchWeekType(date: LocalDate): WeekType {
    val weekInt = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) % 2
    return if (weekInt == 0) {
        WeekType.A
    }else {
        WeekType.B
    }
}