package com.capputinodevelopment.planager.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import org.jetbrains.compose.resources.Font
import planager.composeapp.generated.resources.Res
import planager.composeapp.generated.resources.robotoflex_variable


@Composable
fun RobotoFlexVariable(): FontFamily = FontFamily(
    Font(
        Res.font.robotoflex_variable,
        variationSettings = FontVariation.Settings(
            *arrayOf(
                FontVariation.grade(10),
                FontVariation.weight(850),
                FontVariation.width(30f),
                FontVariation.slant(-20f)
            )
        )
    )
)