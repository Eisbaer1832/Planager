package com.example.indiwarenative

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64

@RequiresApi(Build.VERSION_CODES.O)
suspend fun getData(): String = withContext(Dispatchers.IO){
    val url = URL("https://www.stundenplan24.de/53102849/mobil/mobdaten/Klassen.xml")
    val username = "schueler"
    val password = "s292q17"

    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"

    val auth = "$username:$password"
    val encodedAuth = Base64.getEncoder().encodeToString(auth.toByteArray(Charsets.UTF_8))
    connection.setRequestProperty("Authorization", "Basic $encodedAuth")

    connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
}
