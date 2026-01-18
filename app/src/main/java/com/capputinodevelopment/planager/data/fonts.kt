package com.capputinodevelopment.planager.data

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import com.capputinodevelopment.planager.R

@OptIn(ExperimentalTextApi::class)
val RobotoFlexVariable = FontFamily(
    Font(
        R.font.robotoflex_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.grade(1000),
            FontVariation.weight(800),
            FontVariation.width(500f),
            FontVariation.slant(0f),
        )
    )
)

@OptIn(ExperimentalTextApi::class)
val RobotoLight = FontFamily(
    Font(
        R.font.robotoflex_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.grade(100),
            FontVariation.weight(300),
            FontVariation.width(300f),
            FontVariation.slant(-50f),
        )
    )
)