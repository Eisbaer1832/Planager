package com.capputinodevelopment.planager

import com.russhwolf.settings.PropertiesSettings
import com.russhwolf.settings.Settings
import java.io.File
import java.util.Properties

actual fun provideSettings(): Settings {
    val file = File("dice_prefs.properties")

    val props = Properties().apply {
        if (file.exists()) {
            file.inputStream().use { load(it) }
        }
    }
    val baseSettings: PropertiesSettings = PropertiesSettings(props)

    fun saveToFile() {
        file.outputStream().use { props.store(it, null) }
    }

    return object : Settings by baseSettings {
        override fun putString(key: String, value: String) {
            baseSettings.putString(key, value)
            saveToFile()
        }

        override fun putBoolean(key: String, value: Boolean) {
            baseSettings.putBoolean(key, value)
            saveToFile()
        }

        override fun remove(key: String) {
            baseSettings.remove(key)
            saveToFile()
        }
    }
}
