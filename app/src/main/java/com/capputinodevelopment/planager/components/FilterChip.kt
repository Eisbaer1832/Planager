package com.capputinodevelopment.planager.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SearchFilterChip(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit
) {
    FilterChip(
        onClick = { onSelectedChange(!selected) },
        label = { Text(text) },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}
