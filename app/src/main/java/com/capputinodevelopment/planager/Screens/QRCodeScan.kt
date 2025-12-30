package com.capputinodevelopment.planager.Screens


import android.Manifest
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.capputinodevelopment.planager.MainActivity
import com.capputinodevelopment.planager.components.FriendCreateDialog
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.dataStore
import com.capputinodevelopment.planager.ui.colors.ThemeStore
import com.capputinodevelopment.planager.ui.theme.colors.blue.blueTheme
import com.capputinodevelopment.planager.ui.theme.colors.green.greenTheme
import com.capputinodevelopment.planager.ui.theme.colors.monetThemes
import com.capputinodevelopment.planager.ui.theme.colors.red.redTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.zaki.dynamic.core.adapter.Material3Adapter
import com.zaki.dynamic.core.controller.ThemeController
import com.zaki.dynamic.core.model.ThemeId
import com.zaki.dynamic.core.provider.DynamicThemeProvider
import com.zaki.dynamic.core.provider.PlatformSystemThemeProvider
import com.zaki.dynamic.core.registry.DefaultThemeRegistryFactory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


class QrCodeScanActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(window)
        setContent {
            val context = LocalContext.current
            val userSettings = UserSettings.getInstance(context.applicationContext)
            val couroutineScope = rememberCoroutineScope()

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
                            title = {Text("QR Code scannen")},
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        context.startActivity(
                                            Intent(
                                                context,
                                                MainActivity::class.java
                                            )
                                        )
                                    }
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                                }
                            }
                        )
                    }
                ){innerPadding ->
                    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)

                    val friendsClasses by userSettings.friendsClass.collectAsState(initial = HashMap())
                    val friends by userSettings.friendsSubjects.collectAsState(initial = HashMap())
                    val createFriendDialog = remember { mutableStateOf(false) }
                    var friendName by remember { mutableStateOf("") }
                    val allFriends = userSettings.friendsSubjects.collectAsState(initial = HashMap())
                    var data by remember { mutableStateOf(QRCodeContent("", "", listOf())) }

                    if (createFriendDialog.value) {
                        FriendCreateDialog({ createFriendDialog.value = false }, {name: String ->
                            createFriendDialog.value = false
                            couroutineScope.launch{userSettings.updateFriendsSubjects(friends)}
                            friendName = name

                            if (!friendName.isEmpty()) {
                                val subjects = hashMapOf<String, Boolean>()
                                for (i in 0..<data.subjects.size) {
                                    subjects[data.subjects[i]] = true
                                }
                                val newFriendSubjects = HashMap(allFriends.value)
                                newFriendSubjects[friendName] = subjects

                                val newClasses = HashMap(friendsClasses)
                                newClasses[friendName] = data.year

                                couroutineScope.launch {
                                    userSettings.updateFriendsClass(newClasses)
                                    userSettings.updateFriendsSubjects(newFriendSubjects)
                                }
                            }
                            context.startActivity(
                                Intent(
                                    context,
                                    MainActivity::class.java
                                )
                            )
                        }, "Freund Erfinden")
                    }

                    if (permissionState.status.isGranted) {
                        QrCodeScanScreen(innerPadding) {
                            println("scanned: $it")
                            createFriendDialog.value = true
                            data = Json.decodeFromString<QRCodeContent>(it)
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(onClick = {permissionState.launchPermissionRequest()  }) {
                                Text("Kamerazugriff erlauben")
                            }
                        }
                    }


                }
            }
        }
    }
}
@Composable
fun QrCodeScanScreen(
    innerPadding: PaddingValues,
    onQrCodeDetected: (String) -> Unit, // Callback to handle detected QR/barcode
) {
    var barcode by rememberSaveable { mutableStateOf<String?>("No Code Scanned") }



    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var qrCodeDetected by remember { mutableStateOf(false) }
    var boundingRect by remember { mutableStateOf<Rect?>(null) }

    val cameraController = remember {
        LifecycleCameraController(context)
    }

    AndroidView(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        factory = { ctx ->
            PreviewView(ctx).apply {
                val options = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()
                val barcodeScanner = BarcodeScanning.getClient(options)

                cameraController.setImageAnalysisAnalyzer(
                    ContextCompat.getMainExecutor(ctx),
                    MlKitAnalyzer(
                        listOf(barcodeScanner),
                        ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                        ContextCompat.getMainExecutor(ctx)
                    ) { result: MlKitAnalyzer.Result? ->

                        // Process the barcode scanning results
                        val barcodeResults = result?.getValue(barcodeScanner)
                        if (!barcodeResults.isNullOrEmpty()) {
                            barcode = barcodeResults.first().rawValue
                            qrCodeDetected = true
                            boundingRect = barcodeResults.first().boundingBox
                        }
                    }
                )

                cameraController.bindToLifecycle(lifecycleOwner)
                this.controller = cameraController
            }
        }
    )

    if (qrCodeDetected) {
        LaunchedEffect(Unit) {
            delay(100)
            onQrCodeDetected(barcode ?: "")
        }
        DrawRectangle(rect = boundingRect)
    }
}


@Composable
fun DrawRectangle(rect: Rect?) {
    val composeRect = rect?.toComposeRect()
    composeRect?.let {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                color = Color.Red,
                topLeft = Offset(it.left, it.top),
                size = Size(it.width, it.height),
                style = Stroke(width = 5f)
            )
        }
    }
}