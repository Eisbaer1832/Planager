package com.example.indiwarenative

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import org.w3c.dom.NodeList


object DataSharer {
    var NavbarSelectedItem by mutableIntStateOf(0)
    var SavedSelectedClass: NodeList? = null
}

