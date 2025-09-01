package com.example.indiwarenative

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.material3.Card
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.indiwarenative.data.UserSettings
import kotlinx.coroutines.flow.first

class RoomWidget : GlanceAppWidget() {
    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val userSettings = UserSettings.getInstance(context)
        val username = userSettings.username.first()

        provideContent {
            Scaffold(
                modifier = GlanceModifier
                    .fillMaxSize()
            ) {
                Text(
                    text = "Hello World $username",
                    style = TextStyle(
                        fontSize = 32.sp,
                        color = ColorProvider(Color.Black),
                        textAlign = TextAlign.Center,
                        ),
                    modifier = GlanceModifier.fillMaxSize() // centers in the box
                )
            }
        }
    }
}

class RoomWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = RoomWidget()
}