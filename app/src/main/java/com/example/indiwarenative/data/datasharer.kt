package com.example.indiwarenative.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlin.collections.HashMap


object DataSharer {
    var doFilter by mutableStateOf(true);
    var NavbarSelectedItem by mutableIntStateOf(0)
    var FilterClass by mutableStateOf("")
    var FilterFriend by mutableStateOf("")
}


val Context.dataStore by preferencesDataStore("user_settings")


class UserSettings private constructor(private val appContext: Context) {
    private val dataStore = appContext.dataStore

    val friendsSubjects: Flow<HashMap<String, HashMap<String, Boolean>>> = dataStore.data.map { preferences ->
        preferences[FRIENDS_SUBJECTS]?.let { json ->
            Json.decodeFromString<HashMap<String, HashMap<String, Boolean>>>(json)
        } ?: HashMap()
    }
    suspend fun updateFriendsSubjects(newMap: HashMap<String, HashMap<String, Boolean>>) {
        dataStore.edit { settings ->
            settings[FRIENDS_SUBJECTS] = Json.encodeToString(newMap)
        }
    }

    val friendsClass: Flow<HashMap<String, String>> = dataStore.data.map { preferences ->
        preferences[FRIENDS_CLASS]?.let { json ->
            Json.decodeFromString<HashMap<String, String>>(json)
        } ?: HashMap()
    }
    suspend fun updateFriendsClass(newMap: HashMap<String, String>) {
        dataStore.edit { settings ->
            settings[FRIENDS_CLASS] = Json.encodeToString(newMap)
        }
    }


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


    val ownClass = dataStore.data.map { preferences ->
        preferences[OWN_CLASS] ?: ""
    }
    suspend fun updateOwnClass(value: String) {
        dataStore.edit { settings ->
            settings[OWN_CLASS] = value
        }
    }

    val schoolID = dataStore.data.map { preferences ->
        preferences[SCHOOL_ID] ?: ""
    }

    suspend fun updateSchoolID(value: String) {
        dataStore.edit { settings ->
            settings[SCHOOL_ID] = value
        }
    }
    val username = dataStore.data.map { preferences ->
        preferences[USERNAME] ?: ""
    }

    suspend fun updateUsername(value: String) {
        dataStore.edit { settings ->
            settings[USERNAME] = value
        }
    }
    val password = dataStore.data.map { preferences ->
        preferences[PASSWORD] ?: ""
    }

    suspend fun updatePassword(value: String) {
        dataStore.edit { settings ->
            settings[PASSWORD] = value
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserSettings? = null

        private val SHOW_TEACHERS = booleanPreferencesKey("show_teachers")
        private val OWN_SUBJECTS = stringPreferencesKey("own_subjects")
        private val OWN_CLASS = stringPreferencesKey("own_class")
        private val FRIENDS_SUBJECTS = stringPreferencesKey("friends_subjects")
        private val FRIENDS_CLASS = stringPreferencesKey("friends_class")
        private val SCHOOL_ID = stringPreferencesKey("school_id")
        private val USERNAME = stringPreferencesKey("username")
        private val PASSWORD = stringPreferencesKey("password")


        fun getInstance(context: Context): UserSettings {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserSettings(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
