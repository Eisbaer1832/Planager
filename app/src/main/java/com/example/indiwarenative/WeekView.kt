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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.indiwarenative.ui.theme.IndiwareNativeTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeekView : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IndiwareNativeTheme {
                Scaffold(
                    topBar = {
                        TopBar("Tagesplan")
                    }, bottomBar = {
                        NavBar()
                    }
                ){ innerPadding ->
                    WeekView(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
@Composable
fun smallLessonCard (lesson: lesson) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Card(
        modifier = Modifier
            .width(screenWidth/6)
            .padding(5.dp)

    ) {
        Text(lesson.subject)
        Text(lesson.room)
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("MutableCollectionMutableState")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekView(name: String, modifier: Modifier = Modifier) {
    var week by remember { mutableStateOf(arrayListOf<ArrayList<lesson>>()) }
    var isLoading by remember { mutableStateOf(true) }
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    var current = LocalDate.now()


    LaunchedEffect(Unit) {
        // loading a full school week
        for (i in 0..4) {
            val currentAsString = current.format(formatter)
            val lesson = getLessons("https://www.stundenplan24.de/53102849/mobil/mobdaten/PlanKl${currentAsString}.xml")
            week.add(lesson)
            println("lesson: " + lesson[0].subject)
            println("gettin from week: " + week[0][0].subject)
            current = current.plusDays(1)
        }
        isLoading = false
    }
    val state = rememberPullToRefreshState()
    val isRefreshing = false
    val onRefresh: () -> Unit = {}//TODO implement refresh behaviour


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
            //TODO actually display a full week
            val context = LocalContext.current
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    Card(
                        modifier = Modifier
                            .width(screenWidth / 6)
                            .padding(5.dp)

                    ) {
                        Text("1")
                    }
                }
                for (i in 0..week.size - 1) {
                    Column {
                        Card(
                            modifier = Modifier
                                .width(screenWidth / 6)
                                .padding(5.dp)

                        ) {
                            Text("Tag")
                        }
                        for (j in 0..week[i].size - 1) {
                            smallLessonCard(week[i][j])
                        }
                    }
                }
            }
        }

    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    IndiwareNativeTheme {
        WeekView("Android")
    }
}