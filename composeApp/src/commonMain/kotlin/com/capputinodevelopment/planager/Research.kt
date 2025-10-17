package com.capputinodevelopment.planager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Room
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capputinodevelopment.planager.components.ResearchSearchBar
import com.capputinodevelopment.planager.components.SearchFilterChip
import com.capputinodevelopment.planager.components.SliderToolBar
import com.capputinodevelopment.planager.components.getSubjectIcon
import com.capputinodevelopment.planager.data.DataSharer
import com.capputinodevelopment.planager.data.DataSharer.roundShape
import com.capputinodevelopment.planager.data.RobotoFlexVariable
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.backend.fixDay
import com.capputinodevelopment.planager.data.getToday
import com.capputinodevelopment.planager.data.lesson
import com.capputinodevelopment.planager.data.research.ResearchWeek
import com.capputinodevelopment.planager.data.research.SearchObject
import com.capputinodevelopment.planager.data.research.getResearchData
import com.capputinodevelopment.planager.ui.theme.IndiwareNativeTheme
import kotlinx.coroutines.withContext


@Composable
fun ResearchHeading(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            fontSize = 40.sp,
            textAlign = TextAlign.Center,
            fontFamily = RobotoFlexVariable,
            text = text
        )
    }

}

@Composable
fun ResearchTeacherCard(
    l: lesson,
    shape: RoundedCornerShape,
    surfaceShape: RoundedCornerShape,
) {
    ElevatedCard (
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        shape = shape
    ){
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier.fillMaxSize().weight(1f),
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                text = (l.pos).toString()
            )

            Surface(
                modifier = Modifier.weight(2.5f),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = surfaceShape
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(MaterialShapes.Cookie7Sided.toShape())
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            modifier = Modifier.size(40.dp),
                            imageVector = getSubjectIcon(l.subject),
                            contentDescription = "Localized description",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        modifier = Modifier.fillMaxSize(),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        text = l.subject
                    )
                }
            }
            Column (modifier = Modifier.weight(2f).fillMaxHeight()) {
                Text(
                    modifier = Modifier.fillMaxSize(),
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    text = l.room
                )

            }
        }
    }
}

@Composable
fun ResearchLessonCard(
    l: lesson,
    showTeacher: Boolean?,
    shape: RoundedCornerShape,
    surfaceShape: RoundedCornerShape,
    isRoom: Boolean = false,
    ) {
    ElevatedCard (
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        shape = shape
    ){
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier.fillMaxSize().weight(1f),
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                text = (l.pos).toString()
            )

            Surface(
                modifier = Modifier.weight(2f),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = surfaceShape
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(MaterialShapes.Cookie7Sided.toShape())
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            modifier = Modifier.size(40.dp),
                            imageVector = getSubjectIcon(l.subject),
                            contentDescription = "Localized description",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Text(
                        modifier = Modifier.fillMaxSize(),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        text = l.teacher
                    )
                }
            }
            Column (modifier = Modifier.weight(2f).fillMaxHeight()) {
                    Text(
                        modifier = Modifier.fillMaxSize(),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        text = l.subject
                    )
                    if (!isRoom) {
                        Text(
                            modifier = Modifier.fillMaxSize(),
                            fontSize = 30.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            text = l.room
                        )
                    }

            }
        }
    }
}


@Composable
fun ResearchView(name: String, modifier: Modifier = Modifier, userSettings: UserSettings) {
    val current = fixDay( getToday())

    var dataToSearch by remember { mutableStateOf(ResearchWeek()) }
    val dayToSearch = DataSharer.SliderState.value

    var isLehrerSelected by remember { mutableStateOf(true) }
    var isRaeumeSelected by remember { mutableStateOf(true) }
    var isKlassenSelected by remember { mutableStateOf(true) }
    val items = remember(dataToSearch, dayToSearch, current, isLehrerSelected, isKlassenSelected, isRaeumeSelected) { ArrayList< SearchObject>() }
    var query by rememberSaveable { mutableStateOf("") }

    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit, dayToSearch) {
        println("daytoSearch $dayToSearch")
        loading = true
        val research = withContext(kotlinx.coroutines.Dispatchers.IO) {
            getResearchData(userSettings, dayToSearch)
        }
        dataToSearch = research
        loading = false
    }

    if (isLehrerSelected) {
        dataToSearch.teachers.values.map { teacher ->
            teacher.days.value[dayToSearch]?.getOrNull(0)?.teacher?.let {
                val searchObject = SearchObject(it, Icons.Default.School)
                if (!items.contains(searchObject))items.add(searchObject)
            }
        }
    }

    if (isRaeumeSelected) {
        dataToSearch.rooms.values.map { room ->
            room.days.value[dayToSearch]?.getOrNull(0)?.room?.let {
                val searchObject = SearchObject(it, Icons.Default.Room)
                if (!items.contains(searchObject))items.add(searchObject) }

        }
    }


    if (isKlassenSelected) {
        val keys = dataToSearch.classes.keys
        for (key in keys) {
            val searchObject = SearchObject(key, Icons.Default.Groups)
            if (!items.contains(searchObject)) items.add(searchObject)
        }
    }


    val filteredItems by remember (items, query){
        derivedStateOf {
            if (query.isEmpty()) {
                items
            } else {
                items.filter { it.name.contains(query, ignoreCase = true) }
            }
        }
    }
    Column {
        ResearchSearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { /* Handle search submission */ },
            searchResults = filteredItems,
            onResultClick = { query = it },
            placeholder = { Text("Daten durchsuchen") },
            leadingIconPassed = { Icon(Icons.Default.Menu, contentDescription = "Search") },
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        )
        if (loading) {
            Row(
                Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
            )
            {
                LoadingIndicator(modifier = Modifier.size(60.dp))
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                SearchFilterChip(
                    "Lehrer",
                    Icons.Default.School,
                    isLehrerSelected
                ) { isLehrerSelected = it }
                Spacer(modifier = Modifier.width(10.dp))
                SearchFilterChip(
                    "Räume",
                    Icons.Default.Room,
                    isRaeumeSelected
                ) { isRaeumeSelected = it }
                Spacer(modifier = Modifier.width(10.dp))
                SearchFilterChip(
                    "Klassen",
                    Icons.Default.Groups,
                    isKlassenSelected
                ) { isKlassenSelected = it }

            }

            if (items.isEmpty()) {
                ResearchHeading("Keine Daten vorhanden")

            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredItems) { item ->
                    if (isLehrerSelected) {
                        if (dataToSearch.teachers.contains(item.name)) {
                            ResearchHeading(item.name)
                            val lessons =
                                dataToSearch.teachers[item.name]?.days?.value[dayToSearch]
                                    ?: arrayListOf()
                            for (j in 0..<lessons.size) @Composable {
                                ResearchTeacherCard(lessons[j], roundShape, roundShape)
                            }
                        }
                    }

                    if (isRaeumeSelected) {
                        if (dataToSearch.rooms.contains(item.name)) {
                            ResearchHeading(item.name)
                            val lessons = dataToSearch.rooms[item.name]?.days?.value[dayToSearch]
                                ?: arrayListOf()
                            for (j in 0..<lessons.size) @Composable {
                                ResearchLessonCard(lessons[j], true, roundShape, roundShape, true)
                            }
                        }
                    }

                    println("showing" + dataToSearch.classes)
                    if (isKlassenSelected) {
                        if (dataToSearch.classes.contains(item.name)) {
                            if (!item.name.isEmpty()) {
                                ResearchHeading(item.name)
                            }
                            println("showing search: $item")
                            val lessons = dataToSearch.classes[item.name]?.days?.value[dayToSearch]
                                ?: arrayListOf()
                            for (j in 0..<lessons.size) @Composable {
                                ResearchLessonCard(lessons[j], true, roundShape, roundShape)
                            }
                        }
                    }
                }
                item{
                    Spacer(Modifier.height(150.dp))
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    IndiwareNativeTheme {
        ResearchView("Android")
    }
}