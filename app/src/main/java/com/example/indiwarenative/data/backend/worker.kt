package com.example.indiwarenative.data.backend

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled._7k
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.indiwarenative.data.UserSettings
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.random.Random



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


class NotificationWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {


    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        var current = LocalDate.now()
        var currentAsString = current.format(formatter)

        //val lessons = getLessons(userSettings, "/mobil/mobdaten/PlanKl${currentAsString}.xml")

        println("executing BG Task")
        sendNotification(
            title = "Reminder",
            message = "This is your scheduled notification!"
        )
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
            .setSmallIcon(R.drawable.ic_menu_send)
            .build()

        // Show notification
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        // Using current time as ID ensures each notification is unique
    }
}

