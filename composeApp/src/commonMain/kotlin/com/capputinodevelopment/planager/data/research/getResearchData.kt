package com.capputinodevelopment.planager.data.research

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.backend.getAllClasses
import com.capputinodevelopment.planager.data.backend.getLessons
import com.capputinodevelopment.planager.data.backend.parseLesson
import com.capputinodevelopment.planager.data.getDayXML
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.parser.Parser
import kotlinx.datetime.DayOfWeek

suspend fun getResearchData(
    userSettings: UserSettings,
    day: DayOfWeek
): ResearchWeek {
    var researchData by mutableStateOf(ResearchWeek())

    val xmlTimeTable = getDayXML(day, userSettings)
    if (xmlTimeTable.isEmpty()) {
        return ResearchWeek()
    }

    val doc: Document = Ksoup.parse(xmlTimeTable, parser = Parser.xmlParser())
    val pls = doc.getElementsByTag("Pl")


    for (i in 0..<pls.size) {
        val stds = pls[i].childNodes
        for (j in 0..<stds.size) {
            val lesson = parseLesson(stds[j].childNodes(), false)
            if (lesson.room.isEmpty()) {
                lesson.room = lesson.subject
            }
            if (!lesson.canceled) { // thaaats subject to change ig
                val teacher = researchData.teachers.getOrPut(lesson.teacher) { Data() }
                val room = researchData.rooms.getOrPut(lesson.room) { Data() }

                val teacherDay = teacher.days.value[day]?:arrayListOf(lesson)
                val roomDay = room.days.value[day]?:arrayListOf(lesson)

                if(!teacherDay.contains(lesson)) {
                    var insertIndexTeacher = teacherDay.indexOfFirst { it.pos >= lesson.pos }
                    insertIndexTeacher = if (insertIndexTeacher == -1) teacherDay.size else insertIndexTeacher
                    teacher.days.value[day]?.add(insertIndexTeacher, lesson)
                }

                if(!roomDay.contains(lesson)) {
                    var insertIndexRoom = roomDay.indexOfFirst { it.pos >= lesson.pos }
                    insertIndexRoom = if (insertIndexRoom == -1) roomDay.size else insertIndexRoom
                    room.days.value[day]?.add(insertIndexRoom, lesson)
                }
            }

        }
    }
    val allClasses = getAllClasses(userSettings, "/mobil/mobdaten/Klassen.xml")?: arrayOf()
    println("day $day")
    for (i in 0..<allClasses.size) {
        val classes= researchData.classes.getOrPut(allClasses[i]) { Data() }
        if (allClasses[i].contains(Regex("\\d")) && !allClasses[i].contains("AG")) {
            val lessons = getLessons(userSettings, day, allClasses[i],  false)
            if (!lessons.isNullOrEmpty()) {
                for (i in 0..<(lessons.size)) {
                    classes.days.value[day]?.add(lessons[i])
                }
            }
        }
    }
    println("terachers: " + researchData.teachers)
    println("classes: " + researchData.classes)
    return researchData
}