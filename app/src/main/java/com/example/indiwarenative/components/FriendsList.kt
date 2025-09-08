package com.example.indiwarenative.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.indiwarenative.data.DataSharer
import com.example.indiwarenative.data.DataSharer.FilterClass
import com.example.indiwarenative.data.Kurs
import com.example.indiwarenative.data.UserSettings
import com.example.indiwarenative.data.backend.getKurse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FriendsList (
    showBottomSheet: MutableState<Boolean>,
    Kurse: ArrayList<Kurs>?,
    userSettings: UserSettings,
    allClasses: Array<String>,

    ) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val friends by userSettings.friendsSubjects.collectAsState(initial = HashMap())
    val friendsClasses by userSettings.friendsClass.collectAsState(initial = HashMap())
    val shouldShowDialog = remember { mutableStateOf(false) }
    val createFriendDialog = remember { mutableStateOf(false) }
    val couroutineScope = rememberCoroutineScope()
    var friendName by remember { mutableStateOf("") }
    var kurse = remember { mutableStateOf(Kurse) }
    if (shouldShowDialog.value) {
        println("friend opening with $friendName")
        SubjectDialog(shouldShowDialog, kurse.value, userSettings, false, friendName)
    }

    if (createFriendDialog.value) {
        FriendCreateDialog({ createFriendDialog.value = false }, {name: String ->
            friends.put(name, HashMap())
            createFriendDialog.value = false
            couroutineScope.launch{userSettings.updateFriendsSubjects(friends)}

        }, "Freund Erfinden")
    }

    if (showBottomSheet.value) {

        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState
        ) {
            Column {
                friends.forEach {friend ->
                    FriendItem(
                        //friends[friend.key].get("class") geht leider nicht :(
                        friend.key, friendsClasses.get(friend.key)?:"",
                        {
                            friendName = friend.key
                            FilterClass = friendsClasses.get(friendName)?: ""
                            couroutineScope.launch {
                                kurse.value = getKurse(userSettings, "/mobil/mobdaten/Klassen.xml", null)?: ArrayList()
                            }
                            shouldShowDialog.value = true;
                        }, {selected -> couroutineScope.launch{
                            FilterClass = selected

                            friendsClasses.put(friend.key, selected)
                            userSettings.updateFriendsClass( friendsClasses)
                        }
                        }, {
                            val updatedFriends = HashMap(friends)
                            updatedFriends.remove(friend.key)
                            couroutineScope.launch {
                                userSettings.updateFriendsSubjects(updatedFriends)
                            }
                        },allClasses)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier
                        .padding(16.dp, 0.dp)
                        .height(60.dp)
                        .weight(1f),
                    onClick = {
                        createFriendDialog.value = true
                    }) {
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier
                                .size(30.dp)
                                .weight(1f)

                        )
                        Text(
                            text= "Freund",
                            modifier =  Modifier
                                .weight(2f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Button(

                    modifier = Modifier
                        .padding(16.dp, 0.dp)
                        .height(60.dp)
                        .weight(1.5f),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet.value = false
                            }
                        }
                    }) {
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Favorite",
                            modifier = Modifier
                                .size(30.dp)
                                .weight(1f)

                        )
                        Text(
                            text= "Fertig",
                            modifier =  Modifier
                                .weight(2f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
