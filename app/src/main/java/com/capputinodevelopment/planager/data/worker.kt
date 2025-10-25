package com.capputinodevelopment.planager.data

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.capputinodevelopment.planager.RoomWidget
import com.capputinodevelopment.planager.data.DataSharer.FilterClass
import com.capputinodevelopment.planager.data.backend.fixDay
import com.capputinodevelopment.planager.data.backend.getLessons
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import java.util.concurrent.TimeUnit


@Composable
fun RegisterWorker() {
    val notificationWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
        .build()

    val widgetWorkRequest = PeriodicWorkRequestBuilder<WidgetWorker>(45, TimeUnit.MINUTES)
        .build()


    WorkManager
        .getInstance(LocalContext.current).enqueueUniquePeriodicWork(
            "notification_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            notificationWorkRequest
        )
    WorkManager
        .getInstance(LocalContext.current).enqueueUniquePeriodicWork(
            "widget_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            widgetWorkRequest
        )
}

class WidgetWorker(context: Context, workerParams: WorkerParameters):
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val userSettings = UserSettings.getInstance(applicationContext)
        val dateNow = LocalDate.now()
        val timeNow = LocalTime.now()
        val current = fixDay(timeNow,dateNow)
        val ownSubjects = userSettings.ownSubjects.first()
        var lessons = getLessons(userSettings, current.dayOfWeek, context = applicationContext)?: arrayListOf(lesson())

        var index = if (current == LocalDate.now()) {
            when {
                timeNow.isBefore(LocalTime.parse("09:15:00")) -> 0
                timeNow.isBefore(LocalTime.parse("11:05:00")) -> 2
                timeNow.isBefore(LocalTime.parse("13:00:00")) -> 4
                timeNow.isBefore(LocalTime.parse("15:30:00")) -> 7
                else -> 9
            }
        } else {
            0
        }

        lessons = lessons.filter { lesson ->
            val key = lesson.subject.substringBefore(" ")
            ownSubjects[key] == true || (!lesson.subject.contains(Regex("\\d")) && FilterClass != "13")
        } as ArrayList<lesson>

        if (index > lessons.size - 1) {
            index = lessons.size - 1
        }

        userSettings.updateDayWidgetCash(lessons)
        userSettings.updateRoomWidgetCash(lessons[index])
        RoomWidget().updateAll(applicationContext)
        return Result.success()

    }
}

class NotificationWorker(context: Context, workerParams: WorkerParameters):
    CoroutineWorker(context, workerParams) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        var current = LocalDate.now()
        val timeNow = LocalTime.now()
        current = current.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        current = fixDay(timeNow, current)
        val userSettings = UserSettings.getInstance(applicationContext)
        val status: Map<String, Boolean> = userSettings.ownSubjects.first()
        val notificationHistory = userSettings.notificationHistory.first()

        val week =  arrayListOf<ArrayList<lesson>>()
        val weekField = WeekFields.of(Locale.getDefault())
        var history = notificationHistory.allreadyNotified
        var startDate = notificationHistory.startDate
        val currentWeek = current.get(weekField.weekBasedYear())

        if (startDate.get(weekField.weekBasedYear()) != currentWeek) {
            startDate = current
            history = arrayListOf()
        }

        (0..4).forEach { i ->
            println("current: " + current.dayOfWeek)
            val lesson = getLessons(userSettings, current.dayOfWeek, context = applicationContext)
            if (lesson != null) {
                week.add(lesson)
                current = current.plusDays(1)
            }
        }
        for ((index, day) in week.withIndex()) {
            var lessons = day.filter { lesson ->
                val key = lesson.subject.substringBefore(" ")
                status[key] == true || (!lesson.subject.contains(Regex("\\d")) && FilterClass != "13")
            } as ArrayList<lesson>
            //println("lessons" + lessons.joinToString())
            for (lesson in lessons) {
                println("lesson $lesson")
                if (lesson.canceled) {
                    var allreadyNotified = false
                    println("history: " + history.joinToString())
                    for (historyLesson in history) {
                        println("allready notified ${historyLesson.day}, $index")
                        if (historyLesson.lesson.pos == lesson.pos && historyLesson.lesson.subject == lesson.subject && historyLesson.day == index) {
                            allreadyNotified = true
                        }
                    }
                    if (!allreadyNotified) {
                        sendNotification(
                            title = "Entfall!",
                            message = "${lesson.subject.substringBefore("fällt")}in der ${lesson.pos}. Stunde fällt aus! \uD83C\uDF89"
                        )
                        history = history.plus(NotificationSubject(lesson, index))
                        println("new history" + history)
                        println("history: " + history.joinToString())
                    }
                }

            }
        }
        userSettings.updateNotificationHistory(NotificationHistory(startDate, history))
        return Result.success()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendNotification(title: String, message: String) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        val channel = NotificationChannel("default_channel", "Default Channel", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)


        // Create notification
        val notification = NotificationCompat.Builder(applicationContext, "default_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.stat_sys_warning)
            .build()

        // Show notification
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        // Using current time as ID ensures each notification is unique
    }
}

