package com.example.indiwarenative

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


object DataSharer {
    var NavbarSelectedItem by mutableIntStateOf(0)
}


class DataStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("storeData")
        val showTeachers = stringPreferencesKey("showTeachers")
    }

    val getData: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[showTeachers] ?: ""
        }

    suspend fun saveData(name: String) {
        context.dataStore.edit { preferences ->
            preferences[showTeachers] = name
        }
    }
}
