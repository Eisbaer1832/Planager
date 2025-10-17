package com.example.planager

import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class lesson(
    val pos: Int = 0,
    val teacher: String = "error",
    val subject: String = "error",
    var room: String = "error",
    val roomChanged: Boolean = false,
    val start: LocalTime? = null,
    val end: LocalTime? = null,
    val canceled: Boolean = false,
    val ag: Boolean = false
)
