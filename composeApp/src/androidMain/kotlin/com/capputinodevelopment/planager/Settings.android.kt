package com.capputinodevelopment.planager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import com.capputinodevelopment.planager.components.SettingsCardEdit
import com.capputinodevelopment.planager.data.DataSharer.roundShape

private lateinit var appContext: Context

@Composable
actual fun NotificationPermissionCheck() {
    var hasPermission = ContextCompat.checkSelfPermission(
        appContext,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

    // notification permission stuff
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(appContext, "Benachrichtigungen aktiviert", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(appContext, "Benachrichtigungen deaktiviert", Toast.LENGTH_SHORT).show()
        }
    }

    if (!hasPermission) SettingsCardEdit(
        "Benachrichtigungen",
        roundShape,
        Icons.Default.Check,
        "Erlauben",
        onclick = {permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)},
    )

}