package com.capputinodevelopment.planager

import android.annotation.SuppressLint
import android.content.Context
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
import com.capputinodevelopment.planager.data.UserSettings
import kotlinx.coroutines.flow.first

class RoomWidget : GlanceAppWidget() {
    @SuppressLint("RestrictedApi")

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val userSettings = UserSettings.getInstance(context)
        val lesson = userSettings.roomWidgetCash.first()

        provideContent {
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
                                text = lesson.subject,
                                style = TextStyle(
                                    color = GlanceTheme.colors.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily.Monospace,
                                ),
                            )
                            Text(
                                text = lesson.room,
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

//TODO implement data updates
class DayWidget : GlanceAppWidget() {
    @SuppressLint("RestrictedApi")

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val userSettings = UserSettings.getInstance(context)
        val lessons = userSettings.dayWidgetCash.first()

        provideContent {
            GlanceTheme{
                Scaffold(
                    backgroundColor = GlanceTheme.colors.background,
                    modifier = GlanceModifier
                        .fillMaxSize()
                ) {
                    Column{
                        TitleBar(
                            title = "Unterricht",
                            startIcon = ImageProvider(R.drawable.ic_notification),
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
                                            style = TextStyle(color = GlanceTheme.colors.onPrimaryContainer),
                                            text = lessons[index].subject,
                                            modifier = GlanceModifier.defaultWeight()
                                        )

                                        Text(
                                            style = TextStyle(color = GlanceTheme.colors.onPrimaryContainer),
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
