package com.example.indiwarenative.data.backend

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.indiwarenative.data.Kurs
import com.example.indiwarenative.data.UserSettings
import com.example.indiwarenative.data.lesson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import java.util.Base64
import javax.xml.parsers.DocumentBuilderFactory

// API Endpoints
// https://www.stundenplan24.de/53102849/mobil/mobdaten/Klassen.xml
// https://www.stundenplan24.de/53102849/mobil/mobdaten/PlanKl20250814.xml -- Format yyyymmdd - 20250814

@RequiresApi(Build.VERSION_CODES.O)
suspend fun fetchTimetable(
    userSettings: UserSettings,
    url: String
): String = withContext(Dispatchers.IO){

    println("using: $url")
    val username = userSettings.username.first()
    val password = userSettings.password.first()
    val schoolID = userSettings.schoolID.first()

    try {
        val connection = URL("https://www.stundenplan24.de/$schoolID$url").openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val auth = "$username:$password"
        val encodedAuth = Base64.getEncoder().encodeToString(auth.toByteArray(Charsets.UTF_8))

        connection.setRequestProperty("Authorization", "Basic $encodedAuth")
        connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }catch (e: Exception) {
        e.printStackTrace()
        ""
    }

}


@RequiresApi(Build.VERSION_CODES.O)
suspend fun getSelectedClass(
    userSettings: UserSettings,
    url: String
    ): Node? {
    val xmlTimeTable = fetchTimetable(userSettings, url)
    if (xmlTimeTable.isEmpty()) {
        return null;
    }
    val xmlRes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlTimeTable.byteInputStream())
    val nodeList = xmlRes.documentElement.getElementsByTagName("Kurz")


    for (i in 0..<nodeList.length) {
        if (nodeList.item(i).textContent == "13") {
            return nodeList.item(i).parentNode // kl node
        }
    }
    return null
}
fun getPart(array: NodeList, name: String): String? {
    for (i in 0..array.length) {
        val child = array.item(i)
        if (child.nodeName == name) return child.textContent
    }
    return null
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun getLessons(userSettings: UserSettings, url: String): ArrayList<lesson>? {
    val receivedClass = getSelectedClass(userSettings, url)

    if (receivedClass == null) {
        println("returning null")
        return null
    }
    val selectedClass = receivedClass.childNodes

    val lessonNodes = selectedClass?.item(5)?.childNodes
    val lessons = ArrayList<lesson>()


    for (i in 0..<lessonNodes!!.length) {

        val l = lessonNodes.item(i).childNodes
        val pos = getPart(l, "St")!!.toInt()
        val start =  getPart(l, "Beginn")
        val end = getPart(l, "Ende")
        var subject = getPart(l, "Fa")
        var canceled = false
        if (subject == "---") {
            canceled = true
            subject = getPart(l, "If").toString()
        }
        val teacher = getPart(l, "Le")
        val room = getPart(l, "Ra")

        val formatter = DateTimeFormatter.ofPattern("H:mm")
        lessons.add(
            lesson(
                pos,
                teacher,
                subject,
                room,
                LocalTime.parse(start, formatter),
                LocalTime.parse(end, formatter),
                canceled
            )
        )
    }
    return lessons
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun getKurse(userSettings: UserSettings, url: String): ArrayList<Kurs>? {
    val receivedClass = getSelectedClass(userSettings, url)
    if (receivedClass == null) {
        return null
    }

    val selectedClass =receivedClass.childNodes
    val kursNodes = selectedClass?.item(3)?.childNodes
    val kurse = ArrayList<Kurs>()

    for ( i in 0..<kursNodes!!.length) {
        var text = kursNodes.item(i).firstChild.textContent
        println("Kurs is named $text")
        val teacher = kursNodes.item(i).firstChild.attributes.getNamedItem("KLe").textContent
        if (text == "") {
            text = kursNodes.item(i).firstChild.textContent
        }

        kurse.add(Kurs(teacher, text))
    }
    println(kurse.joinToString())
    println("finished loading kurse")

    return kurse
}