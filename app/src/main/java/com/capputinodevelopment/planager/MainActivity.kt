
package com.capputinodevelopment.planager
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import com.capputinodevelopment.planager.data.DataSharer.FilterFriend
import com.capputinodevelopment.planager.data.DataSharer.doFilter
import com.capputinodevelopment.planager.data.backend.getLessons
import com.capputinodevelopment.planager.components.NavBar
import com.capputinodevelopment.planager.components.TopBar
import com.capputinodevelopment.planager.components.getSubjectIcon
import com.capputinodevelopment.planager.data.DataSharer
import com.capputinodevelopment.planager.data.DataSharer.FilterClass
import com.capputinodevelopment.planager.data.DataSharer.Kurse
import com.capputinodevelopment.planager.data.GlobalPlan.days
import com.capputinodevelopment.planager.data.RobotoFlexVariable
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.backend.fixDay
import com.capputinodevelopment.planager.data.backend.getKurse
import com.capputinodevelopment.planager.data.backend.registerWorker
import com.capputinodevelopment.planager.data.lesson
import com.capputinodevelopment.planager.ui.theme.IndiwareNativeTheme
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            registerWorker()

            IndiwareNativeTheme {
                var currentScreen by remember { mutableStateOf(0) }
                Scaffold(
                    topBar = {
                        when (currentScreen) {
                            0 -> TopBar("Tagesplan", true)
                            1 -> TopBar("Wochenplan", true)
                            2 -> TopBar("Einstellungen", false)


                        }
                    }, bottomBar = {
                        NavBar(currentScreen) { currentScreen = it } }
                ){ innerPadding ->
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            slideInHorizontally { width -> width }.togetherWith(slideOutHorizontally { width -> -width })
                        }
                    ) { screen ->
                        when (screen) {
                            0 -> Greeting(name = "Android", modifier = Modifier.padding(innerPadding))
                            1 -> WeekView(modifier = Modifier.padding(innerPadding))
                            2 -> Settings(modifier = Modifier.padding(innerPadding))
                        }
                    }

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

            textAlign = TextAlign.Center,
            fontSize = 25.sp,
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
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    val roomColor =  if (l.roomChanged) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                    Text(
                        modifier = Modifier.fillMaxSize(),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                        text = l.room,
                        color = roomColor
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
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val userSettings = UserSettings.getInstance(context.applicationContext)
    val showTeacher by userSettings.showTeacher.collectAsState(initial = false)
    var lessons by remember { mutableStateOf<ArrayList<lesson>?>(null) }
    var ags by remember { mutableStateOf<ArrayList<lesson>?>(null) }

    val coroutineScope = rememberCoroutineScope()
    var current = LocalDate.now()
    val ownClass by userSettings.ownClass.collectAsState(initial = String())
    val onboarding by userSettings.onboarding.collectAsState(initial = null)
    var loading by remember { mutableStateOf<Boolean>(true) }

    println("starting main activity")

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
    current = fixDay(timeNow, current)
    println("current" + current.dayOfWeek)

    LaunchedEffect(onboarding) {
        if (onboarding == true) {
            println("doing onboarding")
            context.startActivity(Intent(context, Onboarding::class.java))
        }
    }
    LaunchedEffect(Unit, filter) {
        loading = true
        if (Kurse.isEmpty()) {
            Kurse = getKurse(userSettings, current.dayOfWeek, null)?: ArrayList()
        }
        println("getting DayData for " + current.dayOfWeek)
        lessons = getLessons(userSettings, current.dayOfWeek)
        ags = getLessons(userSettings, current.dayOfWeek, "AG")?:arrayListOf()
        loading = false
    }
    val state = rememberPullToRefreshState()
    var isRefreshing = false
    val onRefresh: () -> Unit = {
        isRefreshing = true
        days = mutableStateOf(
            mutableMapOf(
                DayOfWeek.MONDAY to "",
                DayOfWeek.TUESDAY to "",
                DayOfWeek.WEDNESDAY to "",
                DayOfWeek.THURSDAY to "",
                DayOfWeek.FRIDAY to ""
            )
        )
        coroutineScope.launch {lessons =
            getLessons(userSettings, current.dayOfWeek)
        }
        isRefreshing = false
    }

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
        if (loading || isRefreshing) {
            Row(
                Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
            )
            {
                LoadingIndicator(modifier = Modifier.size(60.dp))
            }
        } else {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            )
            {

                Row(
                    modifier = Modifier
                        .padding(vertical = 30.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        fontSize = 40.sp,
                        fontFamily = RobotoFlexVariable,
                        text = current.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL_STANDALONE ,java.util.Locale.GERMANY),
                    )
                }

                var currentLessons = lessons
                var lastPos = 0

                if (doFilter && !FilterClass.isEmpty()) {

                    // show subject if its not filtered or it doesnt contain in number since that would be a mandatory class subject (hopefully)
                    currentLessons = currentLessons?.filter { status.value[it.subject.substringBefore(" ")] == true || !it.subject.contains(Regex("\\d"))  && FilterClass != "13"} as ArrayList<lesson>?
                }else{
                    currentLessons = currentLessons?.filter { !it.ag } as ArrayList<lesson>?
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