package com.example.indiwarenative.data

import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate

class notificationSubject(
    val lesson: lesson,
    val day: Int
)
class notificationHistory (
    val startDate: LocalDate,
    val allreadyNotified: ArrayList<notificationSubject> = arrayListOf()
)