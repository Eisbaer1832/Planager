package com.capputinodevelopment.planager.data

import com.capputinodevelopment.planager.data.backend.LocalDateSerializer
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
class NotificationSubject(
    val lesson: lesson,
    val day: Int
)
@Serializable
data class NotificationHistory (
    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,
    val allreadyNotified: List<NotificationSubject> = emptyList()
)
