package com.capputinodevelopment.planager

import android.os.Bundle
import android.util.MutableBoolean
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.LocalContext
import androidx.glance.appwidget.lazy.LazyColumn
import com.capputinodevelopment.planager.components.ResearchSearchBar
import com.capputinodevelopment.planager.components.getSubjectIcon
import com.capputinodevelopment.planager.data.DataSharer.bottomShape
import com.capputinodevelopment.planager.data.DataSharer.lessons
import com.capputinodevelopment.planager.data.DataSharer.roundShape
import com.capputinodevelopment.planager.data.DataSharer.topShape
import com.capputinodevelopment.planager.data.GlobalPlan.days
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.backend.getLessons
import com.capputinodevelopment.planager.data.lesson
import com.capputinodevelopment.planager.data.research.ResearchWeek
import com.capputinodevelopment.planager.data.research.Teacher
import com.capputinodevelopment.planager.data.research.getResearchData
import com.capputinodevelopment.planager.ui.theme.IndiwareNativeTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.serializersModuleOf
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.math.round

class Research : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IndiwareNativeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ResearchView(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ResearchLessonCard(
    l: lesson,
    showTeacher: Boolean?,
    shape: RoundedCornerShape,
    surfaceShape: RoundedCornerShape
) {
    ElevatedCard (
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
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
fun ResearchView(name: String, modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current //somehow it knows 2 different types of context, so DO NOT REMOVE the explicit call
    val userSettings = remember { UserSettings.getInstance(context.applicationContext) }
    val current = LocalDate.now()
    var dataToSearch by remember { mutableStateOf(ResearchWeek()) }


    LaunchedEffect(Unit) {
        dataToSearch = getResearchData(userSettings,context, current.dayOfWeek)
    }
    var query by rememberSaveable { mutableStateOf("") }

    val items = remember(dataToSearch, current) {
        dataToSearch.teachers.values.mapNotNull { teacher ->
            teacher.days.value[current.dayOfWeek]?.getOrNull(0)?.teacher
        }
    }


    val filteredItems by remember (items, query){
        derivedStateOf {
            if (query.isEmpty()) {
                items
            } else {
                items.filter { it.contains(query, ignoreCase = true) }
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ResearchSearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { /* Handle search submission */ },
            searchResults = filteredItems,
            onResultClick = { query = it },
            placeholder = { Text("Lehrer durchsuchen") },
            leadingIconPassed = { Icon(Icons.Default.Menu, contentDescription = "Search") },
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            leadingContent = { Icon(Icons.Filled.School, "") },
            modifier = Modifier.wrapContentHeight()
        )

        Column(modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
            for (i in 0..<filteredItems.size) {
                if (dataToSearch.teachers.contains(filteredItems[i])) {
                    println("ititem ${filteredItems[i]}")
                    val lessons = dataToSearch.teachers[filteredItems[i]]?.days?.value[current.dayOfWeek] ?: arrayListOf()
                    for (j in 0..<lessons.size) @Composable {
                        ResearchLessonCard(lessons[j], true, roundShape, roundShape)
                    }
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