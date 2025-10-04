package com.capputinodevelopment.planager.data.research

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.capputinodevelopment.planager.data.DataSharer.FilterClass
import com.capputinodevelopment.planager.data.DataSharer.Kurse
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.backend.getAllClasses
import com.capputinodevelopment.planager.data.backend.getLessons
import com.capputinodevelopment.planager.data.backend.parseLesson
import com.capputinodevelopment.planager.data.getDayXML
import com.capputinodevelopment.planager.data.lesson
import com.capputinodevelopment.planager.orderWeek
import java.time.DayOfWeek
import java.time.LocalDate
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.arrayListOf
import kotlin.collections.mapOf
import kotlin.to

suspend fun getResearchData(
    userSettings: UserSettings,
    context: Context,
    day: DayOfWeek
): ResearchWeek {
    var researchData by mutableStateOf(ResearchWeek())

    val xmlTimeTable = getDayXML(day, userSettings, context)
    if (xmlTimeTable.isEmpty()) {
        return ResearchWeek();
    }

    val xmlRes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlTimeTable.byteInputStream())
    val pls = xmlRes.documentElement.getElementsByTagName("Pl")


    for (i in 0..<pls.length) {
        val stds = pls.item(i).childNodes
        for (j in 0..<stds.length) {
            val lesson = parseLesson(stds.item(j).childNodes, false)
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
    val allClasses = getAllClasses(userSettings, "/mobil/mobdaten/Klassen.xml", context)?: arrayOf(String())
    println("day $day")
    for (i in 0..<allClasses.size) {
        val classes= researchData.classes.getOrPut(allClasses[i]) { Data() }
        if (allClasses[i].contains(Regex("\\d"))) {
            val lessons = getLessons(userSettings, day, allClasses[i], context)
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