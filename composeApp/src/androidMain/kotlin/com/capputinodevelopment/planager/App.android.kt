package com.capputinodevelopment.planager

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.Settings

private lateinit var appContext: Context

fun initAppContext(context: Context) {
    appContext = context.applicationContext
}

actual fun provideSettings(): Settings {
    return SharedPreferencesSettings(
        appContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    )
}
