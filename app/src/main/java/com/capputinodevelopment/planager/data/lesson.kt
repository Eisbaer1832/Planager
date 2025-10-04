package com.capputinodevelopment.planager.data

import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class lesson  constructor(
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
