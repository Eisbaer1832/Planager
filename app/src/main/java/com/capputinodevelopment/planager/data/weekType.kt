package com.capputinodevelopment.planager.data

import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

enum class weekType {
    A, B, AB
}

fun fetchWeekType(): weekType {
    val weekInt = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) % 2
    return if (weekInt == 0) {
        weekType.A
    }else {
        weekType.B
    }
}