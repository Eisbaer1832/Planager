package com.capputinodevelopment.planager.data.backend

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.capputinodevelopment.planager.data.DataSharer.FilterClass
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.NotificationHistory
import com.capputinodevelopment.planager.data.NotificationSubject
import com.capputinodevelopment.planager.data.lesson
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import java.util.concurrent.TimeUnit



@Composable
fun registerWorker() {
    val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
        .build()

    WorkManager
        .getInstance(LocalContext.current).enqueueUniquePeriodicWork(
            "notification_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
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

        var week =  arrayListOf<ArrayList<lesson>>()
        val weekField = WeekFields.of(Locale.getDefault())
        var history = notificationHistory.allreadyNotified
        var startDate = notificationHistory.startDate
        val currentWeek = current.get(weekField.weekBasedYear())

        if (startDate.get(weekField.weekBasedYear()) != currentWeek) {
            startDate = current
            history = arrayListOf()
        }

        for (i in 0..4) {
            println("current: " + current.dayOfWeek)
            val lesson = getLessons(userSettings,current.dayOfWeek, context = applicationContext)
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default_channel",
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

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

