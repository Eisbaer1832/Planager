package com.capputinodevelopment.planager.ui.colors

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.russhwolf.settings.Settings
import com.zaki.dynamic.core.model.ThemeSelection
import com.zaki.dynamic.core.persistance.ThemeStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class ThemeStore(
    private val dataStore: DataStore<Preferences>
) : ThemeStore {
    private val KEY = stringPreferencesKey("theme.selection.json")
    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun load(): ThemeSelection? {
        return dataStore.data.firstOrNull()?.get(KEY)?.let {
            json.decodeFromString<ThemeSelection>(it)
        }
    }

    override suspend fun save(selection: ThemeSelection) {
        dataStore.edit { preferences ->
            preferences[KEY] = json.encodeToString(selection)
        }
    }
}