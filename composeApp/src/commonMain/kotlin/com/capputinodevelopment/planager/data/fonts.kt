package com.capputinodevelopment.planager.data

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily

@OptIn(ExperimentalTextApi::class)
val RobotoFlexVariable = FontFamily(
    Font(
        "font/robotoflex_variable.ttf",
        variationSettings = FontVariation.Settings(
            FontVariation.grade(10),
            FontVariation.weight(850),
            FontVariation.width(30f),
            FontVariation.slant(-20f),
        )
    )
)