package com.example.indiwarenative

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
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
    url: String
): String = withContext(Dispatchers.IO){
    println("using: $url")
    val username = "schueler"
    val password = "s292q17"

    val connection = URL(url).openConnection() as HttpURLConnection
    connection.requestMethod = "GET"

    val auth = "$username:$password"
    val encodedAuth = Base64.getEncoder().encodeToString(auth.toByteArray(Charsets.UTF_8))
    connection.setRequestProperty("Authorization", "Basic $encodedAuth")

    connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
}


@RequiresApi(Build.VERSION_CODES.O)
suspend fun getSelectedClass(url: String): Node? {
    println("getSelecedClass $url")
    val XmlRes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fetchTimetable(url).byteInputStream())
    val nodeList = XmlRes.documentElement.getElementsByTagName("Kurz")


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
suspend fun getLessons(url: String): ArrayList<lesson> {
    println("getLessons using $url")

    var selectedClass = getSelectedClass(url)?.childNodes


    val lessonNodes = selectedClass?.item(5)?.childNodes
    var lessons = ArrayList<lesson>()


    for (i in 0..<lessonNodes!!.length) {

        val l = lessonNodes.item(i).childNodes
        val pos = getPart(l, "St")!!.toInt()
        val start =  getPart(l, "Beginn")
        val end = getPart(l, "Ende")
        var subject = getPart(l, "Fa").toString()
        var canceled = false;
        if (subject == "---") {
            canceled = true
            subject = getPart(l, "If").toString()
        }
        println(subject)
        val teacher = getPart(l, "Le")
        val room = getPart(l, "Ra")

        val formatter = DateTimeFormatter.ofPattern("H:mm")
        lessons.add(lesson(pos, teacher, subject,  room, LocalTime.parse(start, formatter), LocalTime.parse(end, formatter), canceled))
    }
    return lessons
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun getKurse(url:String): ArrayList<Kurs> {
    var selectedClass = getSelectedClass(url)?.childNodes


    val KursNodes = selectedClass?.item(3)?.childNodes
    println(selectedClass?.item(3)?.textContent)
    var Kurse = ArrayList<Kurs>()

    for ( i in 0..<KursNodes!!.length) {
        var text = KursNodes.item(i).firstChild.textContent
        println("Kurs is named $text")
        var teacher = KursNodes.item(i).firstChild.attributes.getNamedItem("KLe").textContent
        if (text == "") {
            text = KursNodes.item(i).firstChild.textContent
        }

        Kurse.add(Kurs(teacher,text))
    }
    println(Kurse.joinToString())
    println("finished loading Kurse")

    return Kurse
}