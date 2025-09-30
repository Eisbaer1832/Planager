package com.capputinodevelopment.planager.data.research

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.capputinodevelopment.planager.data.DataSharer.FilterClass
import com.capputinodevelopment.planager.data.GlobalPlan.researchData
import com.capputinodevelopment.planager.data.UserSettings
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
) {


    val xmlTimeTable = getDayXML(day, userSettings, context)
    if (xmlTimeTable.isEmpty()) {
        return;
    }

    val xmlRes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlTimeTable.byteInputStream())
    val pls = xmlRes.documentElement.getElementsByTagName("Pl")


    for (i in 0..<pls.length) {
        val stds = pls.item(i).childNodes
        for (j in 0..<stds.length) {
            val lesson = parseLesson(stds.item(j).childNodes, false)
            val teacher = researchData.teachers.getOrPut(lesson.teacher) { Teacher() }
            teacher.days.value[day]?.add(lesson)
        }
    }
    println("ergebnis: " + researchData.teachers["KVN"]?.days?.value?.get(day))
}