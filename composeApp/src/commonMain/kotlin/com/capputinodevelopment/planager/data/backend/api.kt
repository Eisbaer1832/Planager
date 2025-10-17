package com.capputinodevelopment.planager.data.backend

import com.capputinodevelopment.planager.Platform
import com.capputinodevelopment.planager.data.DataSharer.FilterClass
import com.capputinodevelopment.planager.data.Kurs
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.getDayXML
import com.capputinodevelopment.planager.data.getKurseXML
import com.capputinodevelopment.planager.data.lesson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import com.capputinodevelopment.planager.data.getToday
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus
import kotlin.io.encoding.Base64


expect suspend fun fetchStoreDB(): String
// API Endpoints
// https://www.stundenplan24.de/53102849/mobil/mobdaten/Klassen.xml
// https://www.stundenplan24.de/53102849/mobil/mobdaten/PlanKl20250814.xml -- Format yyyymmdd - 20250814


fun fixDay(current: LocalDateTime): LocalDate {
    val day = current.date
    val time = current.time
    val endOfDay = LocalTime.parse("19:00:00")
    var returnCurrent = day

    if (day.dayOfWeek == DayOfWeek.SATURDAY) {
        returnCurrent = day.plus(2,DateTimeUnit.DAY)
    }else if (current.dayOfWeek == DayOfWeek.SUNDAY) {
        returnCurrent = day.plus(1,DateTimeUnit.DAY)
    }

    if (time > endOfDay) returnCurrent = day.plus(2,DateTimeUnit.DAY)

    return returnCurrent
}

suspend fun fetchTimetable(
    userSettings: UserSettings,
    url: String,
    localFilterClass: String? = null,
): String {

    println("using: $url for outgoing network call")
    val username = userSettings.username.first()
    val password = userSettings.password.first()
    val schoolID = userSettings.schoolID.first()


    if (username == "google" && password == "google" && schoolID == "google") {
        return fetchStoreDB()
    }

    val connection = URL("https://www.stundenplan24.de/$schoolID$url").openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    val auth = "$username:$password"
    val encodedAuth = Base64.getEncoder().encodeToString(auth.toByteArray(Charsets.UTF_8))
    connection.setRequestProperty("Authorization", "Basic $encodedAuth")
    connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }





}
suspend fun getAllClasses(
    userSettings: UserSettings,
    url: String,
    context: Context

): Array<String>? {
    val xmlTimeTable = getKurseXML(userSettings, context)
    if (xmlTimeTable.isEmpty()) {
        return null
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
        return null
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
    var startT = getToday()
    if (start?.isEmpty() == false) {
        startT = LocalTime.parse(start, formatter)
    }
    var endT = getToday()
    if (end?.isEmpty() == false) {
        endT = LocalTime.parse(end, formatter)
    }
    return lesson(
        pos,
        teacher,
        subject,
        room,
        roomChanged,
        startT,
        endT,
        canceled,
        isAg
    )
}
suspend fun getLessons(userSettings: UserSettings, day: DayOfWeek, localFilterClass: String? = null, context: Context, fetchAgs:Boolean = true): ArrayList<lesson>? {
    val receivedClass = getSelectedClass(userSettings, day, localFilterClass, context)

    var agClasses: NodeList? = null

    if (fetchAgs) {
        agClasses = getSelectedClass(userSettings, day, "AG", context)?.childNodes?.item(5)?.childNodes
    }
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

        if (fetchAgs && agClasses != null) {
            if (lastPos != lesson.pos) {
                lastPos = lesson.pos
                for (j in 0..<agClasses.length) {
                    val agL = agClasses.item(j).childNodes
                    val ag = parseLesson(agL, true)
                    if (ag.pos == lastPos || i >= lessonNodes.length - 1) {
                        lessons.add(ag)
                    }
                    println("AG Namen ${ag.subject}")
                }
            }
        }


    }

    return lessons
}

suspend fun getKurse(userSettings: UserSettings, day: DayOfWeek, localFilterClass: String? = null, context: Context): ArrayList<Kurs>? {
    val receivedClass = getSelectedClass(userSettings, day, localFilterClass, context) ?: return null

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