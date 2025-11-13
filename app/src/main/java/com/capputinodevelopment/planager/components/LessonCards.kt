package com.capputinodevelopment.planager.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LabelImportant
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capputinodevelopment.planager.data.lesson


@Composable
fun TimestampCard(l: lesson, shape: RoundedCornerShape) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        shape = shape,
        modifier = Modifier
            .width(90.dp)
            .padding(start = 5.dp, end = 10.dp)
            .height(80.dp)
    ){
        Row(verticalAlignment = Alignment.CenterVertically) {
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
            .padding(end = 10.dp)
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
    surfaceShape: RoundedCornerShape,
    customColor: String
) {


    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),

        modifier = Modifier.padding(end = 5.dp),
        shape = shape
    ){
        var primaryColor = MaterialTheme.colorScheme.primary
        var primaryColorContainer = MaterialTheme.colorScheme.primaryContainer
        var onprimary = MaterialTheme.colorScheme.onPrimary
        if (l.custom && customColor == "Tertiärfarbe") {
            primaryColor = MaterialTheme.colorScheme.tertiary
            primaryColorContainer = MaterialTheme.colorScheme.tertiaryContainer
            onprimary = MaterialTheme.colorScheme.onTertiary
        }

        Column{
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.width(180.dp),
                    color = primaryColorContainer,
                    shape = surfaceShape
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(MaterialShapes.Cookie7Sided.toShape())
                                .background(primaryColor),
                            contentAlignment = Alignment.Center
                        ){

                            var subject = l.subject
                            if (l.replacementSubject != null) {
                                subject += " " + l.replacementSubject
                            }
                            Icon(
                                modifier = Modifier.size(40.dp),
                                imageVector = getSubjectIcon(subject),
                                contentDescription = "Localized description",
                                tint = onprimary
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if(showTeacher?:false) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = l.teacher
                        )
                    }

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

        }
    }
}

@Composable
fun SmallLessonCard (
    lesson: lesson,
    editing: Boolean,
    customColor: String,
    onClick: () -> Unit,) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // saving the modifier externally for switching the clickable action on and off
    //standart
    var activeMod = Modifier
        .width(screenWidth / 6)
        .height(80.dp)
        .padding(3.dp)

    //on edit
    if (editing && lesson.custom) {
        activeMod = Modifier
            .width(screenWidth / 6)
            .height(80.dp)
            .padding(3.dp)
            .clickable {
                if (editing) {
                    println("launching edit dialog")
                    onClick()
                }
            }

    }
    Card(
        modifier = activeMod
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var primaryColor = MaterialTheme.colorScheme.primaryContainer
            if (lesson.custom && customColor == "Tertiärfarbe") {
                println("using tertiary")
                primaryColor = MaterialTheme.colorScheme.tertiaryContainer
            }
            Surface  (
                color = primaryColor,
                modifier = Modifier.fillMaxWidth()
            ){
                var subject: AnnotatedString = buildAnnotatedString {append(lesson.subject) }

                if (lesson.replacementSubject != null) {
                    subject = buildAnnotatedString {
                        withStyle(style = SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                            append(lesson.subject)
                        }
                        append(lesson.replacementSubject)
                    }
                }
                Text(
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = subject
                )
            }
            if (editing && lesson.custom) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment= Alignment.Center
                ) {
                    Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(35.dp))
                }
            }else {
                Text(
                    text = lesson.teacher.replace("\n", "")
                )
                val roomColor =  if (lesson.roomChanged) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary

                Text(
                    text = lesson.room,
                    color = roomColor

                )
            }
        }
    }
}


@Composable
fun SmallLessonCardCanceled (lesson: lesson) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    var text = lesson.subject
    text = text
        .replace(Regex("fällt aus"), "")
        .replace(Regex("Herr"), "")
        .replace(Regex("Frau"), "")
    val textArray = text.split("  ") //yes actually 2 spaces

    Card(
        modifier = Modifier
            .width(screenWidth / 6)
            .padding(3.dp)
            .height(70.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.error,
            ) {
                Text(
                    fontSize = 19.sp,
                    style = TextStyle(textDecoration = TextDecoration.LineThrough),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = textArray[0]
                )
            }

            var text = ""
            if (textArray.size > 1) {
                text = textArray[1]
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = text
            )

        }
    }

}

