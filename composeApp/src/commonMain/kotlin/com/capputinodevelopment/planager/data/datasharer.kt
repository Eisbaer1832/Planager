package com.capputinodevelopment.planager.data

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SliderState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.capputinodevelopment.planager.data.backend.fixDay
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlin.collections.HashMap
import kotlin.concurrent.Volatile
import com.russhwolf.settings.set
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


object DataSharer {
    var doFilter by mutableStateOf(true)
    var NavbarSelectedItem by mutableIntStateOf(0)
    var FilterClass by mutableStateOf("")
    var SliderState = mutableStateOf(fixDay(getToday()).dayOfWeek)
    var FilterFriend by mutableStateOf("")
    var Kurse by mutableStateOf(ArrayList<Kurs>())
    var AGs by mutableStateOf(ArrayList<Kurs>())
    val topShape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)
    val bottomShape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp)
    val roundShape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp)
    val neutralShape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp)
    var lessons by mutableStateOf( ArrayList<lesson>())
}



class UserSettings private constructor(private val settings: Settings) {

    companion object {
        @Volatile
        private var INSTANCE: UserSettings? = null

        @OptIn(InternalCoroutinesApi::class)
        private val INSTANCE_LOCK: SynchronizedObject = Any() as SynchronizedObject

        @OptIn(InternalCoroutinesApi::class)
        fun getInstance(settings: Settings): UserSettings {
            return INSTANCE ?: kotlin.run {
                synchronized(INSTANCE_LOCK) {
                    INSTANCE ?: UserSettings(settings).also { INSTANCE = it }
                }
            }
        }

        // Shared Json instance for all encode/decode
        val JsonFormat = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            prettyPrint = false
        }

        // KMP-safe helper to decode JSON with default fallback
        inline fun <reified T> decodeOrDefault(json: String, defaultJson: String): T {
            return try {
                JsonFormat.decodeFromString(json.ifEmpty { defaultJson })
            } catch (_: Exception) {
                JsonFormat.decodeFromString(defaultJson)
            }
        }
    }

    // --- Friends Subjects ---
    val friendsSubjects: Flow<HashMap<String, HashMap<String, Boolean>>> = flow {
        val json = settings.getString("friends_subjects", "{}")
        emit(decodeOrDefault(json, "{}"))
    }

    fun updateFriendsSubjects(newMap: HashMap<String, HashMap<String, Boolean>>) {
        settings["friends_subjects"] = JsonFormat.encodeToString(newMap)
    }

    // --- Friends Class ---
    val friendsClass: Flow<HashMap<String, String>> = flow {
        val json = settings.getString("friends_class", "{}")
        emit(decodeOrDefault(json, "{}"))
    }

    fun updateFriendsClass(newMap: HashMap<String, String>) {
        settings["friends_class"] = JsonFormat.encodeToString(newMap)
    }

    // --- Notification History ---
    val notificationHistory: Flow<NotificationHistory> = flow {
        val json = settings.getString("notification_history", "{}")
        emit(decodeOrDefault(json, JsonFormat.encodeToString(NotificationHistory(getToday().date, emptyList()))))
    }

    fun updateNotificationHistory(newList: NotificationHistory) {
        settings["notification_history"] = JsonFormat.encodeToString(newList)
    }

    // --- Day Widget Cache ---
    val dayWidgetCache: Flow<ArrayList<lesson>> = flow {
        val json = settings.getString("day_widget_cache", "[]")
        emit(decodeOrDefault(json, "[]"))
    }

    fun updateDayWidgetCache(newList: ArrayList<lesson>) {
        settings["day_widget_cache"] = JsonFormat.encodeToString(newList)
    }

    // --- Room Widget Cache ---
    val roomWidgetCache: Flow<lesson> = flow {
        val json = settings.getString("room_widget_cache", "{}")
        emit(decodeOrDefault(json, JsonFormat.encodeToString(lesson())))
    }

    fun updateRoomWidgetCache(newValue: lesson) {
        settings["room_widget_cache"] = JsonFormat.encodeToString(newValue)
    }

    // --- Own Subjects ---
    val ownSubjects: Flow<HashMap<String, Boolean>> = flow {
        val json = settings.getString("own_subjects", "{}")
        emit(decodeOrDefault(json, "{}"))
    }

    fun updateOwnSubjects(newMap: HashMap<String, Boolean>) {
        settings["own_subjects"] = JsonFormat.encodeToString(newMap)
    }

    // --- Simple Booleans and Strings ---
    val showTeacher: Flow<Boolean> = flow { emit(settings.getBoolean("show_teachers", false)) }
    fun updateShowTeachers(value: Boolean) { settings["show_teachers"] = value }

    val ownClass: Flow<String> = flow { emit(settings.getString("own_class", "")) }
    fun updateOwnClass(value: String) { settings["own_class"] = value }

    val schoolID: Flow<String> = flow { emit(settings.getString("school_id", "")) }
    fun updateSchoolID(value: String) { settings["school_id"] = value }

    val username: Flow<String> = flow { emit(settings.getString("username", "")) }
    fun updateUsername(value: String) { settings["username"] = value }

    val password: Flow<String> = flow { emit(settings.getString("password", "")) }
    fun updatePassword(value: String) { settings["password"] = value }

    val onboarding: Flow<Boolean> = flow { emit(settings.getBoolean("onboarding", true)) }
    fun updateOnboarding(value: Boolean) { settings["onboarding"] = value }
}