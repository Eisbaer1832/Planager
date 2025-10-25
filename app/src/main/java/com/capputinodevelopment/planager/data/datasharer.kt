package com.capputinodevelopment.planager.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SliderState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.capputinodevelopment.planager.data.backend.fixDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlin.collections.HashMap


@SuppressLint("MutableCollectionMutableState")
object DataSharer {
    var doFilter by mutableStateOf(true)
    var NavbarSelectedItem by mutableIntStateOf(0)
    var FilterClass by mutableStateOf("")
    var SliderState = mutableStateOf(fixDay(LocalTime.now(), LocalDate.now()).dayOfWeek)
    var FilterFriend by mutableStateOf("")
    var Kurse by mutableStateOf(ArrayList<Kurs>())
    var AGs by mutableStateOf(ArrayList<Kurs>())
    val topShape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)
    val bottomShape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp)
    val roundShape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp)
    val neutralShape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp)
    var lessons by mutableStateOf( ArrayList<lesson>())
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

    val notificationHistory: Flow<NotificationHistory> = dataStore.data.map { preferences ->
        preferences[NOTIFICATION_HISTORY]?.let { json ->
            Json.decodeFromString<NotificationHistory>(json)
        }?: NotificationHistory(LocalDate.now(), emptyList())
    }

    suspend fun updateRoomWidgetCash(newList: lesson) {
        dataStore.edit { settings ->
            settings[ROOM_WIDGET_CACHE] = Json.encodeToString(newList)
        }
    }
    val dayWidgetCash: Flow<ArrayList<lesson>> = dataStore.data.map { preferences ->
        preferences[DAY_WIDGET_CACHE]?.let { json ->
            Json.decodeFromString<ArrayList<lesson>>(json)
        }?: arrayListOf(lesson())
    }

    suspend fun updateDayWidgetCash(newList: ArrayList<lesson>) {
        dataStore.edit { settings ->
            settings[DAY_WIDGET_CACHE] = Json.encodeToString(newList)
        }
    }
    val roomWidgetCash: Flow<lesson> = dataStore.data.map { preferences ->
        preferences[ROOM_WIDGET_CACHE]?.let { json ->
            Json.decodeFromString<lesson>(json)
        }?: lesson()
    }

    suspend fun updateNotificationHistory(newList: NotificationHistory) {
        dataStore.edit { settings ->
            settings[NOTIFICATION_HISTORY] = Json.encodeToString(newList)
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

    val onboarding = dataStore.data.map { preferences ->
        preferences[ONBOARDING] ?: true
    }

    suspend fun updateOnboarding(value: Boolean) {
        dataStore.edit { settings ->
            settings[ONBOARDING] = value
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
        private val ONBOARDING = booleanPreferencesKey("onboarding")
        private val NOTIFICATION_HISTORY = stringPreferencesKey("notification_history")
        private val ROOM_WIDGET_CACHE = stringPreferencesKey("room_widget_cache")
        private val DAY_WIDGET_CACHE = stringPreferencesKey("day_widget_cache")

        fun getInstance(context: Context): UserSettings {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserSettings(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
