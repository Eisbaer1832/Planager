package com.capputinodevelopment.planager.data

import com.capputinodevelopment.planager.data.backend.LocalTimeSerializer
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class lesson(
    val pos: Int = 0,
    val teacher: String = "error",
    val subject: String = "error",
    var room: String = "error",
    val roomChanged: Boolean = false,
    @Serializable(with = LocalTimeSerializer::class)
    val start: LocalTime? = null,
    @Serializable(with = LocalTimeSerializer::class)
    val end: LocalTime? = null,
    val canceled: Boolean = false,
    val ag: Boolean = false
)
