package com.capputinodevelopment.planager.components


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.capputinodevelopment.planager.data.DataSharer.FilterClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendItem(
    name: String,
    selectedClass: String,
    edit: () -> Unit,
    classEdit: (String) -> Unit,
    delete: () -> Unit,
    allClasses: Array<String>
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


            var expanded by remember { mutableStateOf(false) }
            var selectedOptionText by remember { mutableStateOf(selectedClass) }
            selectedOptionText = selectedClass

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.width(150.dp)
            ) {

                OutlinedTextField(
                    modifier = Modifier.menuAnchor(),
                    readOnly = true,
                    value = selectedOptionText,
                    onValueChange = {},
                    label = { Text("Klasse") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    allClasses.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                selectedOptionText = selectionOption
                                expanded = false
                                classEdit(selectionOption)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }

            }
            IconButton(
                onClick = {delete()},
            ) {
                Icon(Icons.Default.Delete, "delete")
            }
            IconButton(
                onClick = {
                    FilterClass= selectedClass
                    edit() },
            ) {
                Icon(Icons.Default.Settings, "edit")
            }
        }
    }
}