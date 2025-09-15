package com.example.indiwarenative

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.indiwarenative.data.DataSharer.FilterClass
import com.example.indiwarenative.data.DataSharer.lessons
import com.example.indiwarenative.data.UserSettings
import com.example.indiwarenative.data.backend.fixDay
import com.example.indiwarenative.data.backend.getLessons
import com.example.indiwarenative.data.lesson
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

//TODO implement data updates
class RoomWidget : GlanceAppWidget() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi")

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val userSettings = UserSettings.getInstance(context)
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        var current = LocalDate.now()
        val timeNow = LocalTime.now()
        current = fixDay(timeNow, current)
        println("current"+ current)
        var currentAsString = current.format(formatter)

        if (lessons.isEmpty()) {
            lessons = getLessons(userSettings, current.dayOfWeek) ?: arrayListOf()
        }



        var index: Int
        if (current == LocalDate.now()) {
            index = if (timeNow.isBefore(LocalTime.parse("09:15:00"))) {
                0
            }  else if (timeNow.isBefore(LocalTime.parse("11:05:00"))) {
                2
            } else if (timeNow.isBefore(LocalTime.parse("13:00:00"))) {
                4
            } else if (timeNow.isBefore(LocalTime.parse("15:30:00"))) {
                7
            } else {

                9
            }
        }else {
            index = 0
        }
        // if the user has less than 11 lessons a day

        if (index > lessons.size - 1) {
            index = lessons.size - 1
        }

        println("Subject: ${lessons[index].subject}")
        val subject = lessons[index].subject?: "Kein Unterricht heute"
        val room = lessons[index].room?: ""

        provideContent {
            val status= userSettings.ownSubjects.collectAsState(initial = mapOf<String, Boolean>())


            lessons = lessons.filter { lesson ->
                val key = lesson.subject.substringBefore(" ")
                status.value[key] == true || (!lesson.subject.contains(Regex("\\d")) && FilterClass != "13")
            } as ArrayList<lesson>

            GlanceTheme{
                Scaffold(
                    backgroundColor = GlanceTheme.colors.background,
                    modifier = GlanceModifier
                        .fillMaxSize()
                ) {
                    Box (
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column (horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = GlanceModifier.clickable {} //ensures updates on click,
                            ) {
                            Text(
                                text = subject,
                                style = TextStyle(
                                    color = GlanceTheme.colors.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily.Monospace,
                                ),
                            )
                            Text(
                                text = room,
                                style = TextStyle(
                                    color = GlanceTheme.colors.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 70.sp,
                                    fontFamily = FontFamily.Monospace,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun getWidgetData(userSettings: UserSettings, currentAsString: String): ArrayList<lesson> {
    println("Getting widget Data")
    val status: Map<String, Boolean> = userSettings.ownSubjects.first()
    var lessons: ArrayList<lesson> = getLessons(userSettings, LocalDate.now().dayOfWeek) ?: arrayListOf()
    lessons = lessons.filter { lesson ->
        val key = lesson.subject.substringBefore(" ")
        status[key] == true || (!lesson.subject.contains(Regex("\\d")) && FilterClass != "13")
    } as ArrayList<lesson>

    if (lessons.isEmpty()) {
        lessons.add(lesson())
    }
    return lessons
}

//TODO implement data updates
class DayWidget : GlanceAppWidget() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi")

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        val userSettings = UserSettings.getInstance(context)
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        var current = LocalDate.now()
        val timeNow = LocalTime.now()
        current = fixDay(timeNow, current)


        var currentAsString = current.format(formatter)
        var lessons = getWidgetData(userSettings, currentAsString)
        provideContent {
            //println("Setting widget content")
            GlanceTheme{
                Scaffold(
                    backgroundColor = GlanceTheme.colors.background,
                    modifier = GlanceModifier
                        .fillMaxSize()
                ) {
                    Column{
                        TitleBar(
                            title = "Unterricht",
                            startIcon = ImageProvider(R.drawable.ic_launcher_background),
                        )
                        LazyColumn {
                            items(lessons.size) {index ->
                                Column {
                                    Row(
                                        modifier = GlanceModifier
                                            .fillMaxWidth()
                                            .background(GlanceTheme.colors.primaryContainer)
                                            .padding(
                                                vertical = 15.dp,
                                                horizontal = 10.dp
                                            )
                                            .cornerRadius(16.dp)
                                            .clickable {
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = lessons[index].subject,
                                            modifier = GlanceModifier.defaultWeight()
                                        )

                                        Text(
                                            text = lessons[index].room,
                                        )
                                    }
                                    Spacer(modifier = GlanceModifier.height(5.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class RoomWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = RoomWidget()
}

class DayWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DayWidget()
}
