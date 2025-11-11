package com.capputinodevelopment.planager.data

import com.capputinodevelopment.planager.data.backend.LocalTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalTime
import java.util.UUID

@Serializable
data class lesson(
    val pos: Int = 0,
    val teacher: String = "error",
    val subject: String = "keine",
    var room: String = "Daten",
    val roomChanged: Boolean = false,
    @Serializable(with = LocalTimeSerializer::class)
    val start: LocalTime? = null,
    @Serializable(with = LocalTimeSerializer::class)
    val end: LocalTime? = null,
    val canceled: Boolean = false,
    val ag: Boolean = false,
    val custom: Boolean = false,
    val week: WeekType = WeekType.AB,
    val doubleLesson:Boolean = false,
    val id: String = UUID.randomUUID().toString(),
    val replacementSubject: String? = null
)
