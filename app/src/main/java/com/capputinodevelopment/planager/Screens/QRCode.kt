package com.capputinodevelopment.planager.Screens

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.capputinodevelopment.planager.MainActivity
import com.capputinodevelopment.planager.data.RobotoFlexVariable
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.dataStore
import com.capputinodevelopment.planager.ui.colors.ThemeStore
import com.capputinodevelopment.planager.ui.theme.colors.blue.blueTheme
import com.capputinodevelopment.planager.ui.theme.colors.green.greenTheme
import com.capputinodevelopment.planager.ui.theme.colors.monetThemes
import com.capputinodevelopment.planager.ui.theme.colors.red.redTheme
import com.zaki.dynamic.core.adapter.Material3Adapter
import com.zaki.dynamic.core.controller.ThemeController
import com.zaki.dynamic.core.model.ThemeId
import com.zaki.dynamic.core.provider.DynamicThemeProvider
import com.zaki.dynamic.core.provider.PlatformSystemThemeProvider
import com.zaki.dynamic.core.registry.DefaultThemeRegistryFactory
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.brush
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


class QrCodeActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(window)
        setContent {
            val context = LocalContext.current
            val controller = remember {
                val registry = DefaultThemeRegistryFactory.create().apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        registerFamily(monetThemes(context))
                    }
                    registerFamilies(listOf(blueTheme(), redTheme(), greenTheme()))
                }
                ThemeController(
                    registry = registry,
                    store = ThemeStore(context.dataStore),
                    system = PlatformSystemThemeProvider(),
                    defaultThemeId = ThemeId("Monet")
                )
            }
            DynamicThemeProvider(
                controller = controller,
                adapter = Material3Adapter(),
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {Text("QR Code teilen")},
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        context.startActivity(Intent(context, MainActivity::class.java))
                                    }
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                                }
                            }
                        )
                    }
                ){innerPadding ->
                    QrCodeScreen(innerPadding)
                }
            }
        }
    }
}
@Serializable
class QRCodeContent(val version: String, val year: String, val subjects: List<String>)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QrCodeScreen(innerPadding: PaddingValues) {
    // QR Code Setup
    val gradColor1 = MaterialTheme.colorScheme.primaryContainer
    val gradColor2 = MaterialTheme.colorScheme.tertiaryContainer
    val context = LocalContext.current
    val userSettings = remember { UserSettings.getInstance(context.applicationContext) }
    val ownClass = userSettings.ownClass.collectAsState("")
    //convert subjects to array, to safe space in qrcode
    val ownSubjects = userSettings.ownSubjects.collectAsState(hashMapOf()).value.filterValues { it }.keys.toList()
    val customSubjects = userSettings.customSubjects.collectAsState(mutableMapOf())
    val version = "PLANAGER_QR_1.0,"

    val qrData = Json.encodeToString(QRCodeContent(version, ownClass.value, ownSubjects))

    println(qrData)
    val qrCode = rememberQrCodePainter(qrData) {
        shapes {
            ball = QrBallShape.circle()
            darkPixel = QrPixelShape.roundCorners()
            frame = QrFrameShape.roundCorners(.25f)
        }
        colors {
            dark = QrBrush.brush {Brush.linearGradient(listOf(gradColor1, gradColor2))}
            frame = QrBrush.solid(Color.Black)
        }
    }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Card(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
        ) {
            Image(
                modifier = Modifier.weight(1f, fill = false)
                    .aspectRatio(qrCode.intrinsicSize.width / qrCode.intrinsicSize.height)
                    .fillMaxWidth()
                    .padding(15.dp),
                painter = qrCode,
                contentScale = ContentScale.Fit,
                contentDescription = "QR code referring to the example.com website"
            )
        }
    }

}