package com.example.indiwarenative.data

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
class NotificationSubject(
    val lesson: lesson,
    val day: Int
)

@Serializable
class NotificationHistory (
    val startDate: LocalDate,
    val allreadyNotified: ArrayList<NotificationSubject> = arrayListOf()
)