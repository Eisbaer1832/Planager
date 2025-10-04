package com.capputinodevelopment.planager.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LicenseDialog (showBottomSheet: MutableState<Boolean>, ) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState
        ) {
            Column {
                Text(
                    text= "Freund",
                    modifier =  Modifier.weight(2f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
