package com.capputinodevelopment.planager.ui.theme.colors

import android.content.Context
import androidx.compose.ui.graphics.Color

import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import com.zaki.dynamic.core.model.ThemeDefinition
import com.zaki.dynamic.core.model.ThemeFamily
import com.zaki.dynamic.core.model.ThemeId
import com.zaki.dynamic.core.themes.defaultShapes
import com.zaki.dynamic.core.themes.defaultTypography
import com.zaki.dynamic.core.toPalette


val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)



fun monetThemes(context: Context): ThemeFamily {
    val defaultLightTheme = ThemeDefinition(
        id = ThemeId("monet_light"),
        displayName = "System",
        palette = dynamicLightColorScheme(context).toPalette(false),
        typography = defaultTypography(),
        shapes = defaultShapes(),
        meta = mapOf("materialVersion" to "3", "default" to "true")
    )

    val defaultDarkTheme = ThemeDefinition(
        id = ThemeId("monet_dark"),
        displayName = "System",
        palette = dynamicDarkColorScheme(context).toPalette(true),
        typography = defaultTypography(),
        shapes = defaultShapes(),
        meta = mapOf("materialVersion" to "3", "default" to "true")
    )

    val defaultFamily = ThemeFamily(
        id = ThemeId("monet"),
        displayName = "System",
        light = defaultLightTheme,
        dark = defaultDarkTheme
    )
    return defaultFamily
}