package com.example.indiwarenative.components


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FriendItem(
    name: String,
    edit: () -> Unit,
    delete: () -> Unit,
    ) {
    Card(
        shape = RoundedCornerShape(16.dp,16.dp, 16.dp, 16.dp),
        modifier = Modifier.fillMaxWidth().padding(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = name,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = {delete()},
            ) {
                Icon(Icons.Default.Delete, "delete")
            }
            IconButton(
                onClick = {edit()},
            ) {
                Icon(Icons.Default.Edit, "edit")
            }
        }
    }
}