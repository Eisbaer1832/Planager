package com.capputinodevelopment.planager.data.backend

import android.content.Context
import com.capputinodevelopment.planager.R
import com.capputinodevelopment.planager.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.printStackTrace
import kotlin.use

private lateinit var appContext: Context

actual suspend fun fetchStoreDB(): String {
    return withContext(Dispatchers.IO) {
        try {
            appContext.resources.openRawResource(R.raw.plan)
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() } // returns String
        } catch (e: Exception) {
            e.printStackTrace()
            "" // returns empty String on error
        }
    }
}