
package com.example.indiwarenative
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.LabelImportant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.indiwarenative.data.DataSharer.FilterFriend
import com.example.indiwarenative.data.DataSharer.doFilter
import com.example.indiwarenative.data.backend.getLessons
import com.example.indiwarenative.components.NavBar
import com.example.indiwarenative.components.TopBar
import com.example.indiwarenative.data.DataSharer
import com.example.indiwarenative.data.DataSharer.FilterClass
import com.example.indiwarenative.data.UserSettings
import com.example.indiwarenative.data.backend.registerWorker
import com.example.indiwarenative.data.lesson
import com.example.indiwarenative.ui.theme.IndiwareNativeTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            registerWorker()

            IndiwareNativeTheme {
                Scaffold(
                    topBar = {
                        TopBar("Tagesplan", true)
                    }, bottomBar = {
                        NavBar()
                    }
                ){ innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TimestampCard(l: lesson, shape: RoundedCornerShape) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        shape = shape,
        modifier = Modifier
            .width(90.dp)
            .padding(start = 10.dp, end = 10.dp)
            .height(80.dp)
    ){
        Text(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            text = l.pos.toString(),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LessonCardCanceled(l: lesson, shape: RoundedCornerShape)  {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = shape,
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialShapes.Cookie7Sided.toShape())
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    imageVector = Icons.AutoMirrored.Filled.LabelImportant,
                    contentDescription = "Localized description",
                    tint = MaterialTheme.colorScheme.onError
                )
            }
            Text(
                modifier = Modifier.padding(16.dp),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                text = l.subject
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LessonCard(
    l: lesson,
    showTeacher: Boolean?,
    shape: RoundedCornerShape,
    surfaceShape: RoundedCornerShape
) {


    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = shape
    ){
        Column{
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.width(180.dp),
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
                                imageVector = Icons.AutoMirrored.Filled.Assignment,
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
                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        modifier = Modifier.fillMaxSize(),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        text = l.room
                    )
                }
            }
            if (showTeacher == true) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    text = "Lehrer: " + l.teacher
                )
            }
        }
    }
}


@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val userSettings = UserSettings.getInstance(context.applicationContext)
    val showTeacher by userSettings.showTeacher.collectAsState(initial = false)
    var lessons by remember { mutableStateOf<ArrayList<lesson>?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    var current = LocalDate.now()
    val ownClass by userSettings.ownClass.collectAsState(initial = String())
    val onboarding by userSettings.onboarding.collectAsState(initial =true)
    if (onboarding) {
    //    context.startActivity(Intent(context, Onboarding::class.java))
    }

    val filter by remember { DataSharer::FilterClass }

    if (filter.isEmpty()) {
        FilterClass = ownClass
    }


    val status: State<HashMap<String, Boolean>> = if (FilterFriend == "") {
        userSettings.ownSubjects.collectAsState(initial = HashMap())
    } else {
        mutableStateOf(userSettings.friendsSubjects.collectAsState(initial = HashMap()).value.get(FilterFriend)?: HashMap())
    }

    val timeNow = LocalTime.now()
    val endOfDay = LocalTime.parse("19:00:00")
    if (timeNow.isAfter(endOfDay)) {
        current = current.plusDays(1)
    }

    var currentAsString = current.format(formatter)


    LaunchedEffect(Unit, filter) {
        println("launching launch effect")
        lessons = getLessons(userSettings, "/mobil/mobdaten/PlanKl${currentAsString}.xml")
    }
    val state = rememberPullToRefreshState()
    val isRefreshing = false
    val onRefresh: () -> Unit = { coroutineScope.launch {lessons =
        getLessons(userSettings, "/mobil/mobdaten/PlanKl${currentAsString}.xml")
    }}

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

    Box {
        if (lessons == null) {
            Column(
                Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                LoadingIndicator()
            }
        } else {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            )
            {
                var currentLessons = lessons
                var lastPos = 0

                if (doFilter && !FilterClass.isEmpty()) {

                    // show subject if its not filtered or it doesnt contain in number since that would be a mandatory class subject (hopefully)
                    currentLessons = currentLessons?.filter { status.value[it.subject.substringBefore(" ")] == true || !it.subject.contains(Regex("\\d"))  && FilterClass != "13"} as ArrayList<lesson>?
                }
                currentLessons?.forEachIndexed { i, l ->
                        val topShape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)
                        val bottomShape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp)
                        val topSurfaceShape = RoundedCornerShape(16.dp, 0.dp, 0.dp, 0.dp)
                        val bottomSurfaceShape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 16.dp)
                        val neutralShape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp)
                        val rounded = RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp)
                        var numberShape = neutralShape
                        var shape = neutralShape
                        var surfaceShape = neutralShape

                        val pos = l.pos


                        //TODO Clean this mess up
                        if (i + 1 <= currentLessons.size - 1) {
                            if (l.pos < 8) {
                                if (pos % 2 == 0) {
                                    numberShape = bottomShape
                                    if (currentLessons[i + 1].pos > pos) {
                                        shape = bottomShape
                                        surfaceShape = bottomSurfaceShape
                                    }
                                }else {
                                    numberShape = topShape
                                    if (pos > lastPos) {
                                        shape = topShape
                                        surfaceShape = topSurfaceShape
                                    }
                                }
                            }else {
                                if (pos % 2 != 0){
                                    numberShape = bottomShape

                                    if (currentLessons[i + 1].pos > pos) {
                                        shape = bottomShape
                                        surfaceShape = bottomSurfaceShape
                                    }
                                }else {
                                    numberShape = topShape
                                    if (pos > lastPos) {
                                        shape = topShape
                                        surfaceShape = topSurfaceShape
                                    }
                                }

                            }
                        }

                        if (i == currentLessons.size - 1) {
                            shape = bottomShape
                            numberShape = bottomShape
                            surfaceShape = bottomSurfaceShape
                        }

                        if (!doFilter || showTeacher) {
                            numberShape = rounded
                        }

                        if (showTeacher) {
                            surfaceShape = RoundedCornerShape(16.dp, 0.dp, 0.dp, 0.dp)
                        }

                        Row{
                            if (pos > lastPos) {
                                lastPos = pos;
                                TimestampCard(l, numberShape)
                            } else {
                              Spacer(Modifier.width(90.dp))
                            }

                            println(l.subject)
                            if (!l.canceled) {
                                LessonCard(l, showTeacher, shape, surfaceShape)
                            } else {
                                LessonCardCanceled(l, shape)
                            }
                        }
                    }
            }
        }}
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IndiwareNativeTheme {
        Greeting("Android")
    }
}