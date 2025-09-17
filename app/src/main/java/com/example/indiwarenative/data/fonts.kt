package com.example.indiwarenative.data

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import com.example.indiwarenative.R

@OptIn(ExperimentalTextApi::class)
val RobotoFlexVariable = FontFamily(
    Font(
        R.font.robotoflex_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.grade(10),
            FontVariation.weight(850),
            FontVariation.width(30f),
            FontVariation.slant(-20f),
        )
    )
)