package com.capputinodevelopment.planager.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.AssuredWorkload
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Microwave
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.ui.graphics.vector.ImageVector


fun getSubjectIcon(name: String):ImageVector {
    var icon: ImageVector
    val subShort: String = name.take(name.length.coerceAtMost(2)).lowercase()
    when (subShort) {
        "de" -> icon = Icons.Filled.Book
        "ku" -> icon = Icons.Filled.Brush
        "ma" -> icon = Icons.Filled.Calculate
        "en" -> icon = Icons.Filled.Book
        "ph" -> icon = Icons.Filled.Microwave
        "ch" -> icon = Icons.Filled.Science
        "bi" -> icon = Icons.Filled.MonitorHeart
        "la" -> icon = Icons.Filled.AssuredWorkload
        "sf" -> icon = Icons.Filled.Newspaper
        "sn" -> icon = Icons.Default.BeachAccess
        "ge" -> icon = Icons.Default.History
        "if" -> icon = Icons.Default.Terminal
        "mu" -> icon = Icons.Default.MusicNote
        "ek" -> icon = Icons.Default.AirplanemodeActive
        else -> icon = Icons.AutoMirrored.Filled.Assignment
    }
    return icon
}