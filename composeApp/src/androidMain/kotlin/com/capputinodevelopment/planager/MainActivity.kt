package com.capputinodevelopment.planager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.capputinodevelopment.planager.data.RegisterWorker

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initAppContext(this)
        setContent {
            RegisterWorker()
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}