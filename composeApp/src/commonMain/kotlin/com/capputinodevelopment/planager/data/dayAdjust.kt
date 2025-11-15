package com.capputinodevelopment.planager.data

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

fun previousOrSameMonday(date: LocalDate): LocalDate {
    val dayOfWeek = date.dayOfWeek  // kotlinx.datetime.DayOfWeek
    val daysToSubtract = (dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal + 7) % 7
    return date.minus(daysToSubtract, DateTimeUnit.DAY)
}

fun previousOrSame(date: LocalDate, targetDay: DayOfWeek): LocalDate {
    val daysToSubtract = (date.dayOfWeek.ordinal - targetDay.ordinal + 7) % 7
    return date.minus(daysToSubtract, DateTimeUnit.DAY)
}

fun nextOrSame(date: LocalDate, targetDay: DayOfWeek): LocalDate {
    val daysToAdd = (targetDay.ordinal - date.dayOfWeek.ordinal + 7) % 7
    return date.plus(daysToAdd, DateTimeUnit.DAY)
}

