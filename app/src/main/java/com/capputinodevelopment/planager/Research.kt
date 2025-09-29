package com.capputinodevelopment.planager

import android.os.Bundle
import android.util.MutableBoolean
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.capputinodevelopment.planager.components.ResearchSearchBar
import com.capputinodevelopment.planager.data.DataSharer.lessons
import com.capputinodevelopment.planager.data.GlobalPlan.days
import com.capputinodevelopment.planager.data.UserSettings
import com.capputinodevelopment.planager.data.backend.getLessons
import com.capputinodevelopment.planager.data.lesson
import com.capputinodevelopment.planager.ui.theme.IndiwareNativeTheme
import kotlinx.coroutines.launch
import java.time.DayOfWeek

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

@Composable
fun ResearchView(name: String, modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val textFieldState = remember { TextFieldState() }

    var results by remember { mutableStateOf(listOf("Apple", "Banana", "Cherry")) }
    val filters = listOf("Lehrer", "Raum", "Klasse")
    var selectedFilter by rememberSaveable { mutableStateOf(filters[0]) }

    val state = rememberPullToRefreshState()
    var isRefreshing = false
    val onRefresh: () -> Unit = {
        isRefreshing = true
        //TODO implement frefresh
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
            if (!isRefreshing) {
                Column(modifier = Modifier.fillMaxSize()) {
                    ResearchSearchBar(
                        textFieldState = textFieldState,
                        onSearch = {
                        },
                        searchResults = results,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()) {
                        Text("filter")

                        filters.forEach { filter ->
                            Text(filter)
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = { Text(filter) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
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