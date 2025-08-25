package com.example.indiwarenative.components


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp

@Composable
fun FriendItem(
    name: String,
    onConfirmation: () -> Unit,

    ) {
    Card(
        modifier = Modifier
            .height(70.dp)
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.fillMaxSize(),
            text = name
        )
    }
}