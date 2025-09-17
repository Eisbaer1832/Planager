package com.example.indiwarenative

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.indiwarenative.data.LocalTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class lesson  constructor(
    val pos: Int = 0,
    val teacher: String = "error",
    val subject: String = "error",
    val room: String = "error",
    val roomChanged: Boolean = false,
    @Serializable(with = LocalTimeSerializer::class)
    val start: LocalTime? = null,
    @Serializable(with = LocalTimeSerializer::class)
    val end: LocalTime? = null,
    val canceled: Boolean = false
)
