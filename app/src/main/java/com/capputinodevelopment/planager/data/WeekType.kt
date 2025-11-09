package com.capputinodevelopment.planager.data

import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

enum class WeekType {
    A, B, AB
}

fun fetchWeekType(date: LocalDate): WeekType {
    val weekInt = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) % 2
    return if (weekInt == 0) {
        WeekType.A
    }else {
        WeekType.B
    }
}