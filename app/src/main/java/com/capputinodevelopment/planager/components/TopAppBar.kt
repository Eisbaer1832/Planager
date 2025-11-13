package com.capputinodevelopment.planager.components


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.capputinodevelopment.planager.data.DataSharer.FilterFriend
import com.capputinodevelopment.planager.data.DataSharer.doFilter
import com.capputinodevelopment.planager.data.UserSettings

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedButtonSwitch(
    editSubjects: Boolean,
    onClick: () -> Unit,
) {
    AnimatedContent(
        targetState = editSubjects,
        transitionSpec = {
            fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.8f) togetherWith
                    fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 1.2f)
        },
    ) { targetEdit ->
        if (targetEdit) {
            // FilledIconButton when editSubjects = true
            FilledIconButton(onClick = onClick) {
                Icon(Icons.Default.Edit, contentDescription = "edit")
            }
        } else {
            // Standard IconButton when editSubjects = false
            IconButton(onClick = onClick) {
                Icon(Icons.Default.Edit, contentDescription = "edit")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopBar(title: String, showHamburger: Boolean, editSubjects: Boolean = false, showEditMode: Boolean = false, updateEdit: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
            )},

        actions = {
            if (showEditMode) {
                AnimatedButtonSwitch(editSubjects = editSubjects) {
                    updateEdit()
                }
            }
            if (showHamburger) Hamburger()
        },
    )
}

@Composable
fun Hamburger() {
    val context = LocalContext.current
    val userSettings = UserSettings.getInstance(context.applicationContext)
    val expanded = remember { mutableStateOf(false) }
    IconButton(onClick = { expanded.value = !expanded.value }) {
        Icon(Icons.Default.FilterAlt, contentDescription = "More options")
    }

    val friends by userSettings.friendsSubjects.collectAsState(initial = HashMap())

    var selected by remember { mutableStateOf(0) }
    if (FilterFriend == "" && !doFilter) {
        selected = 1 // all subjects
    }else if (FilterFriend != "") {
        friends.keys.forEachIndexed { index, key ->
            println("$key $FilterFriend $index")
            if (FilterFriend == key) {
                println("equal")
                selected = index + 2
            }
        }
    }else {
        selected = 0
    }
    if (expanded.value) {
        MoreOptionsSheet(expanded, userSettings,selected, friends)
    }
}