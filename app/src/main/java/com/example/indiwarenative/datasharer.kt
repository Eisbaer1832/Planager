package com.example.indiwarenative

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream


object DataSharer {
    var doFilter by mutableStateOf(true);
    var NavbarSelectedItem by mutableIntStateOf(0)
}


val Context.dataStore by preferencesDataStore("user_settings")


class UserSettings private constructor(private val appContext: Context) {
    private val dataStore = appContext.dataStore


    val ownSubjects: Flow<HashMap<String, Boolean>> = dataStore.data.map { preferences ->
        preferences[OWN_SUBJECTS]?.let { json ->
            Json.decodeFromString<HashMap<String, Boolean>>(json)
        } ?: HashMap()
    }
    suspend fun updateOwnSubjects(newMap: HashMap<String, Boolean>) {
        dataStore.edit { settings ->
            settings[OWN_SUBJECTS] = Json.encodeToString(newMap)
        }
    }

    val showTeacher = dataStore.data.map { preferences ->
        preferences[SHOW_TEACHERS] ?: false
    }

    suspend fun updateShowTeachers(value: Boolean) {
        dataStore.edit { settings ->
            settings[SHOW_TEACHERS] = value
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserSettings? = null

        private val SHOW_TEACHERS = booleanPreferencesKey("show_teachers")
        private val OWN_SUBJECTS = stringPreferencesKey("own_subjects")

        fun getInstance(context: Context): UserSettings {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserSettings(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
