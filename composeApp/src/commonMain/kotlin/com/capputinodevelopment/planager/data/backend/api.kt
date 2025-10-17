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
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.parser.Parser
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.util.collections.getValue
import io.ktor.utils.io.InternalAPI
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus


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
    val client = HttpClient()

    println("using: $url for outgoing network call")
    val username = userSettings.username.first()
    val password = userSettings.password.first()
    val schoolID = userSettings.schoolID.first()


    if (username == "google" && password == "google" && schoolID == "google") {
        return fetchStoreDB()
    }

    return try {
        client.get(url) {
            username.let { user ->
                password.let { pwd ->
                    headers.append(HttpHeaders.Authorization, "$user:$pwd")
                }
            }
        }.bodyAsText()
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    } finally {
        client.close()
    }



}
suspend fun getAllClasses(
    userSettings: UserSettings,
    url: String,

): Array<String>? {
    val xmlTimeTable = getKurseXML(userSettings)
    if (xmlTimeTable.isEmpty()) {
        return null
    }
    val doc: Document = Ksoup.parse(xmlTimeTable, parser = Parser.xmlParser())

    val nodeList = doc.getElementsByTag("Kurz").asList()

    var allClasses = arrayOf<String>()
    for (i in 0..<nodeList.size) {
        allClasses = allClasses.plus(nodeList[i].text())
    }
    return allClasses
}

suspend fun getSelectedClass(
    userSettings: UserSettings,
    day: DayOfWeek,
    localFilterClass: String? = null,
): Node? {

    val xmlTimeTable = getDayXML(day, userSettings)
    if (xmlTimeTable.isEmpty()) {
        return null
    }
    //println("filter: " + localFilterClass)
    val filter = localFilterClass ?: FilterClass

    val doc: Document = Ksoup.parse(xmlTimeTable, parser = Parser.xmlParser())
    val nodeList = doc.getElementsByTag("Kurz").asList()


    for (i in 0..<nodeList.size) {
        if (nodeList[i].text() == filter) {
            return nodeList[i].parentNode() // kl node
        }
    }
    return null
}

fun getPart(array: List<Node>, name: String): String? {
    for (i in 0..array.size) {
        val child = array[i]
        if (child.nodeName() == name) return child.nodeName()
    }
    return null
}

fun parseLesson(l: List<Node>, isAg: Boolean): lesson {
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
    var roomChanged = false
    for (i in 0..l.size) {
        val child = l[i]
        if (child.nodeName() == "Ra") {
            if (child.attributes()["RaAe"] != "") roomChanged = true
        }
    }
    var startT: LocalTime = getToday().time
    if (start?.isEmpty() == false) {
        startT = LocalTime.parse(start)
    }
    var endT: LocalTime = getToday().time
    if (end?.isEmpty() == false) {
        endT = LocalTime.parse(end)
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
suspend fun getLessons(userSettings: UserSettings, day: DayOfWeek, localFilterClass: String? = null, fetchAgs:Boolean = true): ArrayList<lesson>? {
    val receivedClass = getSelectedClass(userSettings, day, localFilterClass)

    var agClasses: List<Node> = listOf()

    if (fetchAgs) {
        agClasses = getSelectedClass(userSettings, day, "AG")?.childNodes()?.get(5)?.childNodes()!!
    }
    val lessons = ArrayList<lesson>()

    if (receivedClass == null) {
        println("returning null")
        return null
    }
    val selectedClass = receivedClass.childNodes()

    val lessonNodes = selectedClass[5].childNodes()

    var lastPos = 0
    for (i in 0..<lessonNodes.size) {
        val l = lessonNodes[i].childNodes()
        val lesson = parseLesson(l, false)
        lessons.add(lesson)

        if (fetchAgs) {
            if (lastPos != lesson.pos) {
                lastPos = lesson.pos
                for (j in 0..<agClasses.size) {
                    val agL = agClasses[j].childNodes()
                    val ag = parseLesson(agL, true)
                    if (ag.pos == lastPos || i >= lessonNodes.size - 1) {
                        lessons.add(ag)
                    }
                    println("AG Namen ${ag.subject}")
                }
            }
        }


    }

    return lessons
}

@OptIn(InternalAPI::class)
suspend fun getKurse(userSettings: UserSettings, day: DayOfWeek, localFilterClass: String? = null,): ArrayList<Kurs>? {
    val receivedClass = getSelectedClass(userSettings, day, localFilterClass) ?: return null

    val selectedClass =receivedClass.childNodes()



    val kursNodes = selectedClass[3].childNodes()
    val kurse = ArrayList<Kurs>()


    // normal classes (P/W)
    var normalClasses = selectedClass[4].childNodes()
    for (i in 0..<normalClasses.size) {
        val c = normalClasses[i].firstChild()
        val teacher = c?.attributes()["UeLe"]?:""
        val name = c?.attributes()["UeFa"]?:""
        if (name.contains("-P") || name.contains("-W")) {
            kurse.add(Kurs(teacher, name))

        }
    }
    //Kurse
    for ( i in 0..<kursNodes.size) {
        val text = kursNodes[i].firstChild()?.nodeName()?:""
        println("Kurs is named $text")
        val teacher = kursNodes[i].firstChild()?.attributes()["KLe"]?:""

        kurse.add(Kurs(teacher, text))
    }
    println(kurse.joinToString())
    println("finished loading kurse")

    return kurse
}