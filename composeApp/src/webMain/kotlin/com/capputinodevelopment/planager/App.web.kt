package com.capputinodevelopment.planager

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings

actual fun provideSettings(): Settings {
    return  StorageSettings()
}