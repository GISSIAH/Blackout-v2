package com.example.bottomnavbardemo.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.bottomnavbardemo.database.AppDatabase
import com.example.bottomnavbardemo.models.notification
import com.example.bottomnavbardemo.ui.theme.Gray900


@Composable
fun NotificationScreen(navController: NavController){
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize()) {
        Column() {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)) {
                Box(modifier = Modifier.zIndex(0f)) {
                    Column(verticalArrangement = Arrangement.Center) {
                        IconButton(
                            modifier= Modifier
                                .alpha(ContentAlpha.medium)
                            ,
                            onClick = {
                                navController.popBackStack()

                            }) {
                            Icon(modifier = Modifier
                                .size(25.dp)
                                ,imageVector = Icons.Outlined.ArrowBack,contentDescription = "back Icon",tint= Gray900)
                        }
                    }

                    Box(modifier = Modifier.zIndex(99f)) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),horizontalArrangement = Arrangement.Center) {
                            Text(modifier = Modifier,text = "Notifications",style = MaterialTheme.typography.h2)
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(vertical = 15.dp,horizontal = 10.dp)) {
                notificationList()
            }
        }

    }
}

@Composable
fun notificationList(){
    val context = LocalContext.current
    val notificationDao by lazy { AppDatabase.getDatabase(context).notificationDao() }
    val notis = notificationDao.getAll()
    LazyColumn(modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth()
        .fillMaxHeight()){
        items(notis){ noti ->
            Notification(noti)
        }
    }
}

@Composable
fun Notification(noti:notification){
    Card(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxWidth()
            .height(100.dp)
        ,
        elevation = 8.dp,
        shape = RoundedCornerShape(corner = CornerSize(4.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp)) {
            Text(text = noti.title,style = MaterialTheme.typography.h5)
            Text(modifier = Modifier.padding(vertical = 3.dp),text = noti.body,style = MaterialTheme.typography.body1)
        }
    }
}