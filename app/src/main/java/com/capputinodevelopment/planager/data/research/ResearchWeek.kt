package com.capputinodevelopment.planager.data.research

import androidx.compose.runtime.mutableStateOf
import com.capputinodevelopment.planager.data.research.Teacher

data class ResearchWeek  constructor(
    var teachers: MutableMap<String, Teacher> = mutableMapOf<String, Teacher>()
)