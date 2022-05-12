package com.example.bottomnavbardemo.screens

import android.content.Context
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bottomnavbardemo.ui.theme.Gray500
import com.example.bottomnavbardemo.ui.theme.Gray700
import com.example.bottomnavbardemo.ui.theme.Green
import com.example.bottomnavbardemo.ui.theme.Red
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Column {
            Text(
                modifier=Modifier.padding(horizontal = 10.dp,vertical = 20.dp),
                text = "Settings",
                style=MaterialTheme.typography.h1
            )

            NotificationGroup()

            LocationGroup()
        }

    }
}

@Composable
@Preview
fun SettingsScreenPreview() {
    SettingsScreen()
}

@Composable
fun LocationGroup(){
    val context = LocalContext.current
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 20.dp)) {
        Text(text = "Your Location",style = MaterialTheme.typography.h3)
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Current Area",style = MaterialTheme.typography.h4,)
            Text(modifier = Modifier.padding(horizontal = 50.dp),text="${getArea(context)}",style=MaterialTheme.typography.h4,)
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Current Group",style = MaterialTheme.typography.h4)
            Text(modifier = Modifier.padding(horizontal = 55.dp),text="Group ${getGroupName(context)?.uppercase()}",style=MaterialTheme.typography.h4,)
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),horizontalArrangement = Arrangement.Center) {
            Button(colors = ButtonDefaults.buttonColors(
                backgroundColor = Red,
                contentColor = White
            ),onClick = { setUserPreferences(context)}) {
                Text(text = "Reset Location")
            }
        }


    }
}

fun getArea(context:Context): String? {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    return sharedPreferences.getString("area", "")
}






@Composable
fun NotificationGroup(){

    val context = LocalContext.current
    val groupTopic="group${getGroupName(context)?.uppercase()}"
    val mCheckedState = remember{ mutableStateOf(getNotificationState(context))}
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = sharedPreferences.edit()
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 5.dp)) {
        Text(text = "Group Notifications",style = MaterialTheme.typography.h3)
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.Bottom) {
            Text(modifier = Modifier.padding(vertical = 2.dp),text="Receive updates about your schedule group.", style=MaterialTheme.typography.body1,)
            Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                Switch(colors =SwitchDefaults.colors(
                    checkedThumbColor = Green,
                    uncheckedThumbColor = Red
                ),modifier = Modifier.size(20.dp),checked = mCheckedState.value, onCheckedChange = {
                    editor.putBoolean("groupNotify",it)
                    mCheckedState.value = it
                    editor.commit()
                    if (it){
                        if (groupTopic != null) {
                            subscribeToGroup(groupTopic,context)
                        }
                    }else{
                        if (groupTopic != null) {
                            unsubscribeToGroup(groupTopic,context)
                        }
                    }
                })
            }

        }

    }
}



fun getNotificationState(context: Context):Boolean{
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    return sharedPreferences.getBoolean("groupNotify",true)
}

fun subscribeToGroup(group:String,context:Context){
    Firebase.messaging.subscribeToTopic(group)
        .addOnCompleteListener { task ->
            var msg = "Subscribed to group notifications"
            if (!task.isSuccessful) {
                msg = "Failed to subscribe to group notifications"
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
}

fun unsubscribeToGroup(group: String,context:Context){
    Firebase.messaging.unsubscribeFromTopic(group).addOnCompleteListener { task ->
        var msg = "Unsubscribed to group notifications"
        if (!task.isSuccessful) {
            msg = "Failed to cancel subscription"
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}








