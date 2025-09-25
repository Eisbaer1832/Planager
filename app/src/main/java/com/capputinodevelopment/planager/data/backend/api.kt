package com.capputinodevelopment.planager.data.backend

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.LocalContext
import com.capputinodevelopment.planager.R
import com.capputinodevelopment.planager.data.DataSharer.FilterClass
import com.capputinodevelopment.planager.data.Kurs
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.getDayXML
import com.capputinodevelopment.planager.data.getKurseXML
import com.capputinodevelopment.planager.data.lesson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import java.util.Base64
import javax.xml.parsers.DocumentBuilderFactory
import androidx.compose.ui.platform.LocalResources

// API Endpoints
// https://www.stundenplan24.de/53102849/mobil/mobdaten/Klassen.xml
// https://www.stundenplan24.de/53102849/mobil/mobdaten/PlanKl20250814.xml -- Format yyyymmdd - 20250814


@RequiresApi(Build.VERSION_CODES.O)
fun fixDay(timeNow: LocalTime?, current: LocalDate): LocalDate {
    val endOfDay = LocalTime.parse("19:00:00")
    var returnCurrent = current

    if (current.dayOfWeek == DayOfWeek.SATURDAY) {
        returnCurrent = current.plusDays(2)
    }else if (current.dayOfWeek == DayOfWeek.SUNDAY) {
        returnCurrent = current.plusDays(1)
    }else if (timeNow != null) {
        if (timeNow.isAfter(endOfDay)) returnCurrent = current.plusDays(1)
    }
    return returnCurrent
}

suspend fun fetchTimetable(
    userSettings: UserSettings,
    url: String,
    localFilterClass: String? = null,
    lContext: Context,
): String = withContext(Dispatchers.IO){

    println("using: $url for outgoing network call")
    val username = userSettings.username.first()
    val password = userSettings.password.first()
    val schoolID = userSettings.schoolID.first()
    if (username == "google" && password == "google" && schoolID == "google") {
        return@withContext try {
            lContext.resources.openRawResource(R.raw.plan)
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
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
suspend fun getAllClasses(
    userSettings: UserSettings,
    url: String,
    context: Context

): Array<String>? {
    val xmlTimeTable = getKurseXML(userSettings, context)
    if (xmlTimeTable.isEmpty()) {
        return null;
    }
    val xmlRes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlTimeTable.byteInputStream())
    val nodeList = xmlRes.documentElement.getElementsByTagName("Kurz")

    var allClasses = arrayOf(String())
    for (i in 0..<nodeList.length) {
        allClasses = allClasses.plus(nodeList.item(i).textContent)
    }
    return allClasses
}

suspend fun getSelectedClass(
    userSettings: UserSettings,
    day: DayOfWeek,
    localFilterClass: String? = null,
    context: Context
): Node? {

    val xmlTimeTable = getDayXML(day, userSettings, context)
    if (xmlTimeTable.isEmpty()) {
        return null;
    }
    //println("filter: " + localFilterClass)
    val filter = localFilterClass ?: FilterClass

    val xmlRes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlTimeTable.byteInputStream())
    val nodeList = xmlRes.documentElement.getElementsByTagName("Kurz")


    for (i in 0..<nodeList.length) {
        if (nodeList.item(i).textContent == filter) {
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

fun parseLesson(l: NodeList, isAg: Boolean): lesson {
    val pos = getPart(l, "St")!!.toInt()
    val start =  getPart(l, "Beginn")
    val end = getPart(l, "Ende")
    var subject = getPart(l, "Fa")?:""
    var canceled = false
    if (subject == "---") {
        canceled = true
        subject = getPart(l, "If")?:""
    }
    val teacher = getPart(l, "Le")?:""
    val room = getPart(l, "Ra")?:""
    val formatter = DateTimeFormatter.ofPattern("H:mm")
    var roomChanged = false
    for (i in 0..l.length) {
        val child = l.item(i)
        if (child != null) {
            if (child.nodeName == "Ra") {
                if (child.attributes.getNamedItem("RaAe") != null) roomChanged = true
            }
        }
    }
    return lesson(
        pos,
        teacher,
        subject,
        room,
        roomChanged,
        LocalTime.parse(start, formatter),
        LocalTime.parse(end, formatter),
        canceled,
        isAg
    )
}
suspend fun getLessons(userSettings: UserSettings, day: DayOfWeek, localFilterClass: String? = null, context: Context): ArrayList<lesson>? {
    val receivedClass = getSelectedClass(userSettings, day, localFilterClass, context)
    val agClasses = getSelectedClass(userSettings, day, "AG", context)?.childNodes?.item(5)?.childNodes

    val lessons = ArrayList<lesson>()

    if (receivedClass == null) {
        println("returning null")
        return null
    }
    val selectedClass = receivedClass.childNodes

    val lessonNodes = selectedClass?.item(5)?.childNodes

    var lastPos = 0
    for (i in 0..<lessonNodes!!.length) {
        val l = lessonNodes.item(i).childNodes
        val lesson = parseLesson(l, false)
        lessons.add(lesson)

        if (lastPos !=lesson.pos ) {
            lastPos = lesson.pos
            for (j in 0..<agClasses!!.length) {
                val agL = agClasses.item(j).childNodes
                val ag = parseLesson(agL, true)
                if (ag.pos == lastPos || i >= lessonNodes.length -1) {
                    lessons.add(ag)
                }
                println("AG Namen ${ag.subject}")
            }
        }


    }

    return lessons
}

suspend fun getKurse(userSettings: UserSettings, day: DayOfWeek, localFilterClass: String? = null, context: Context): ArrayList<Kurs>? {
    val receivedClass = getSelectedClass(userSettings, day, localFilterClass, context)
    if (receivedClass == null) {
        return null
    }

    val selectedClass =receivedClass.childNodes



    val kursNodes = selectedClass?.item(3)?.childNodes
    val kurse = ArrayList<Kurs>()


    // normal classes (P/W)
    var normalClasses = selectedClass?.item(4)?.childNodes
    for (i in 0..<normalClasses!!.length) {
        val c = normalClasses.item(i).firstChild
        val teacher = c.attributes.getNamedItem("UeLe").textContent
        val name = c.attributes.getNamedItem("UeFa").textContent
        if (name.contains("-P") || name.contains("-W")) {
            kurse.add(Kurs(teacher, name))

        }
    }
    //Kurse
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