package com.capputinodevelopment.planager.data.research

import androidx.compose.runtime.mutableStateOf
import com.capputinodevelopment.planager.data.research.Data

data class ResearchWeek  constructor(
    var teachers: MutableMap<String, Data> = mutableMapOf<String, Data>(),
    var rooms: MutableMap<String, Data> = mutableMapOf<String, Data>(),
    var classes: MutableMap<String, Data> = mutableMapOf<String, Data>()
)