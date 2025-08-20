package com.example.indiwarenative

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.indiwarenative.ui.theme.IndiwareNativeTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class WeekView : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IndiwareNativeTheme {
                Scaffold(
                    topBar = {
                        TopBar("Wochenplan")
                    }, bottomBar = {
                        NavBar()
                    }
                ){ innerPadding ->
                    WeekView(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun SmallLessonCard (lesson: lesson) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Card(
        modifier = Modifier
            .width(screenWidth / 6)
            .padding(3.dp)
            .height(70.dp)


    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Surface  (
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = lesson.subject
                )
            }
            Text(
                text = lesson.teacher
            )
            Text(
                text = lesson.room
            )
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("MutableCollectionMutableState")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val userSettings = UserSettings.getInstance(context.applicationContext)
    val showTeacher by userSettings.showTeacher.collectAsState(initial = false)
    val subjectsToShow by userSettings.ownSubjects.collectAsState(initial = HashMap())

    var week by remember { mutableStateOf(arrayListOf<ArrayList<lesson>>()) }
    var isLoading by remember { mutableStateOf(true) }
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    var current = LocalDate.now()
    current = current.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    var orderedWeek: HashMap<Int, ArrayList<ArrayList<lesson>>> = HashMap()

    LaunchedEffect(Unit) {
        // loading a full school week
        for (i in 0..4) {
            val currentAsString = current.format(formatter)
            val lesson = getLessons("https://www.stundenplan24.de/53102849/mobil/mobdaten/PlanKl${currentAsString}.xml")
            week.add(lesson)
            current = current.plusDays(1)
        }
        //largest  = SubjectCountPerLesson(week)
        isLoading = false
        //TODO implement Week reorder
        orderedWeek = orderWeek(week)
    }



    val state = rememberPullToRefreshState()
    val isRefreshing = false
    val onRefresh: () -> Unit = {}//TODO implement refresh behaviour

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                state = state
            )
        },
    ) {
        if (isLoading) {
            Column(
                Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                LoadingIndicator()
            }
        } else {
            val days = arrayOf("Mo.", "Di.", "Mi.", "Do.", "Fr.")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Row {
                    Spacer(modifier = Modifier.width(screenWidth / 6))
                    for (i in 0..<days.size) {
                        Card(
                            modifier = Modifier
                                .width(screenWidth / 6)
                                .padding(3.dp)
                        ) {
                            Text(
                                text = days[i],
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
                for ( pos in 1..orderedWeek.size - 1 ) {
                    Row {
                        Card(
                            modifier = Modifier
                                .width(screenWidth / 6)
                                .height(70.dp)
                                .padding(15.dp, 7.dp, 15.dp, 7.dp,),
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pos.toString(),
                                )
                            }
                        }
                        if (pos == 7) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp)
                                    .padding(3.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Pauseee \uD83C\uDF89",
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            for (i in 0..<(orderedWeek[pos]?.size ?: 0)) {
                                Column {
                                    for (j in 0..<(orderedWeek[pos]?.get(i)?.size ?: 0)) {
                                        println("size is: " + orderedWeek[pos]?.get(i)?.size)
                                        if(subjectsToShow[orderedWeek.get(pos)?.get(i)?.get(j)?.subject ?: true] == true) {
                                            //println("showing in $pos : " + orderedWeek.get(pos)?.get(i)?.get(j)?.subject)
                                            SmallLessonCard(
                                                orderedWeek.get(pos)?.get(i)?.get(j) ?: lesson()
                                            )
                                        }else{
                                            //println("not showing in $pos : " + orderedWeek.get(pos)?.get(i)?.get(j)?.subject)
                                            Spacer(modifier = Modifier.width(configuration.screenWidthDp.dp / 6))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

    }

}

// TODO Check if this is actually implemented? Ig, this should work? Maybe?
fun orderWeek(week: ArrayList<ArrayList<lesson>>): HashMap<Int, ArrayList<ArrayList<lesson>>> {
    var newWeek = HashMap<Int, ArrayList<ArrayList<lesson>>>()

    for (i in 0..week.size - 1){
        var tempArray: ArrayList<lesson> = arrayListOf()
        var lastPos = 1
        for (j in 0..week[i].size - 1){
            val pos = week[i][j].pos
            if (pos > lastPos) {
                var allreadSaved = newWeek.get(lastPos)
                if (allreadSaved == null) {
                    allreadSaved = ArrayList()
                }
                allreadSaved.add(tempArray)
                newWeek.put(lastPos, allreadSaved)
                tempArray = arrayListOf()
                lastPos = pos
            }
            tempArray.add(week[i][j])
            println("Ordered Week Part: " + tempArray[0].subject)
        }
    }
    return newWeek
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    IndiwareNativeTheme {
        WeekView()
    }
}