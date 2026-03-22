package com.capputinodevelopment.planager.data.backend

import com.capputinodevelopment.planager.data.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64

suspend fun getDayFromSearchServer(date: String?, userSettings: UserSettings): String = withContext(
    Dispatchers.IO){
    val server = userSettings.customDB.first()
    val key = userSettings.customDBkey.first()
    try {
        val connection = URL("$server?date=$date").openConnection() as HttpURLConnection

        connection.requestMethod = "GET"

        val auth = "planager:$key"
        val encodedAuth = Base64.getEncoder().encodeToString(auth.toByteArray(Charsets.UTF_8))

        connection.setRequestProperty("Authorization", "Basic $encodedAuth")
        connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText()  }
    } catch (error: Exception) {
        println("could not fetch search data from custome server")
        println(error)

        return@withContext ""
    }
}