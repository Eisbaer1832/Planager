package com.example.indiwarenative.data.backend

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.indiwarenative.data.DataSharer.FilterClass
import com.example.indiwarenative.data.UserSettings
import com.example.indiwarenative.data.lesson
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
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


    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        var current = LocalDate.now()
        val timeNow = LocalTime.now()
        current = fixDay(timeNow, current)
        val userSettings = UserSettings.getInstance(applicationContext)
        val status: Map<String, Boolean> = userSettings.ownSubjects.first()
        var week =  arrayListOf<ArrayList<lesson>>()

        for (i in 0..4) {
            val currentAsString = current.format(formatter)
            val lesson =
                getLessons(
                    userSettings,
                    "" + "/mobil/mobdaten/PlanKl${currentAsString}.xml"
                )
            if (lesson != null) {
                week.add(lesson)
                current = current.plusDays(1)
            }

        }
        for (day in week) {
            var lessons = day.filter { lesson ->
                val key = lesson.subject.substringBefore(" ")
                status[key] == true || (!lesson.subject.contains(Regex("\\d")) && FilterClass != "13")
            } as ArrayList<lesson>
            //println("lessons" + lessons.joinToString())
            for (lesson in lessons) {
                println("lesson $lesson")
                if (lesson.canceled) {
                    sendNotification(
                        title = "Entfall!",
                        message = "${lesson.subject.substringBefore("fällt")}in der ${lesson.pos}. Stunde fällt aus! \uD83C\uDF89"
                    )
                }

            }
        }

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
            .setSmallIcon(R.drawable.ic_delete)
            .build()

        // Show notification
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        // Using current time as ID ensures each notification is unique
    }
}

