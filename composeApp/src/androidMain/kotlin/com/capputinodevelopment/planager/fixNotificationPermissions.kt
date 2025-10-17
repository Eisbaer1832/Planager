package com.capputinodevelopment.planager

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun NotificationPermissionPopup() {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permission denied!", Toast.LENGTH_SHORT).show()
        }
    }

    // Show popup when needed
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Notification Permission") },
        text = { Text("This app would like to send you notifications.") },
        confirmButton = {
            Button(onClick = {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }) {
                Text("Allow")
            }
        },
        dismissButton = {
            Button(onClick = { /* Maybe just dismiss */ }) {
                Text("Deny")
            }
        }
    )
}
