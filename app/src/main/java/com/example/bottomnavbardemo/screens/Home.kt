package com.example.bottomnavbardemo.screens


import android.content.Context
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.bottomnavbardemo.api.ServiceBuilder
import com.example.bottomnavbardemo.models.blackoutModel
import com.example.bottomnavbardemo.navigation.AllScreens
import com.example.bottomnavbardemo.ui.theme.*
import com.example.loadshedding.models.DayGroupSchedule
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime
import java.util.*
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

@Composable
    fun HomeScreen(
    navController: NavController

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column {
                TopSection(navController)
                //MiddleSection()
            }

        }
    }
    @Composable
    @Preview
    fun HomeScreenPreview() {

    }
    @Composable
    fun TopSection(navController: NavController){
        val context = LocalContext.current

        val group =  getGroupName(context)

        if (group != null) {
            Column(modifier = Modifier
                .padding(vertical = 15.dp)
                .fillMaxWidth()){
                val textPaddingModifier  = Modifier.padding(5.dp)
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically) {
                    Text(text = getGreeting(),
                        modifier = textPaddingModifier,
                        style= MaterialTheme.typography.h2,

                    )
                    IconButton(
                        modifier= Modifier
                            .alpha(ContentAlpha.medium)
                            .padding(horizontal = 20.dp),
                        onClick = {
                            navController.navigate(route = AllScreens.Notifications.route)
                        }) {
                        val badgeState = getBadgeState(context)
                        if(badgeState){
                            BadgedBox(badge = { Badge { Text("1+") } }) {
                                Icon(modifier = Modifier.size(30.dp),imageVector = Icons.Outlined.Notifications,contentDescription = "Notification Icon",tint= MaterialTheme.colors.primaryVariant)
                            }
                        }else{

                                Icon(modifier = Modifier.size(30.dp),imageVector = Icons.Outlined.Notifications,contentDescription = "Notification Icon",tint= MaterialTheme.colors.primaryVariant)

                        }


                    }
                }


                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp, vertical = 20.dp)) {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 15.dp)
                            .height(200.dp)
                            .fillMaxWidth()
                        ,
                        elevation = 8.dp,
                        shape = RoundedCornerShape(corner = CornerSize(16.dp))
                    ) {
                        Column(modifier = Modifier.background(Red)) {
                            Text(text = "Today",
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .background(Red),
                                style =  MaterialTheme.typography.h3, color = Color.White
                            )
                            val todaymodel = BlackoutModel(group)
                            BlackoutListScreen(viewModel = todaymodel)

                        }

                    }
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 45.dp)
                            .height(200.dp)
                            .fillMaxWidth()
                        ,
                        elevation = 4.dp,
                        backgroundColor = Color.White,
                        shape = RoundedCornerShape(corner = CornerSize(16.dp))
                    ){
                        Column(modifier = Modifier.background(lightRed)) {
                            Text(text = "Tomorrow",
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .background(lightRed),
                                style =  MaterialTheme.typography.h3, color = Color.Black
                            )
                            TomorrowBlackoutListScreen(viewModel = TomorrowBlackoutModel(group,context))
                        }
                    }


                }
            }
        }
    }

    @Composable
    fun MiddleSection(){
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(modifier = Modifier.padding(horizontal = 10.dp),text = "Summary",style = MaterialTheme.typography.h2)
            Row(modifier = Modifier
                .fillMaxWidth(4f)
                .padding(horizontal = 5.dp)) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp, vertical = 5.dp)
                        .height(150.dp)
                        .weight(2f)
                    ,
                    elevation = 4.dp,
                    backgroundColor = Red,
                    shape = RoundedCornerShape(corner = CornerSize(16.dp))
                ){Column(){
                    Text(text = "Big")
                }}
                Column(modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 5.dp)
                    .height(150.dp)
                    .weight(2f)
                    ) {
                    Row(modifier = Modifier
                        .fillMaxWidth(2f)
                        .padding(horizontal = 5.dp)) {
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .height(75.dp)
                                .weight(2f)
                            ,
                            elevation = 4.dp,
                            backgroundColor = Red,
                            shape = RoundedCornerShape(corner = CornerSize(16.dp))
                        ){Column() {
                            Text(text = "small")
                        }}
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth(2f)
                        .padding(horizontal = 5.dp)) {
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 5.dp, vertical = 5.dp)
                                .height(75.dp)
                                .weight(2f)
                            ,
                            elevation = 4.dp,
                            backgroundColor = Red,
                            shape = RoundedCornerShape(corner = CornerSize(16.dp))
                        ){Column() {
                            Text(text = "small")
                        }}
                    }

                }
            }
        }
        

    }


    @Composable
    fun BlackoutListScreen(
        // pass the view model in this form for convenient testing
        viewModel: BlackoutModel
    ) {
        val context = LocalContext.current

        //Toast.makeText(context, day.toString(), Toast.LENGTH_SHORT).show()
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.padding(vertical = 10.dp),color = Red) {
            BlackoutList(viewModel.blackoutCards,Color.White)
        }
    }
    fun getBadgeState(context: Context):Boolean{
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean("badgeState",true)
    }
    @Composable
    fun TomorrowBlackoutListScreen(
    // pass the view model in this form for convenient testing
     viewModel: TomorrowBlackoutModel
    ) {
    val context = LocalContext.current

    //Toast.makeText(context, day.toString(), Toast.LENGTH_SHORT).show()
    // A surface container using the 'background' color from the theme
    Surface(color = lightRed) {
        BlackoutList(viewModel.blackoutCards, Color.Black)
    }
}
    @Composable
    fun BlackoutList(blackouts: SnapshotStateList<blackoutModel>,textColor:Color) {
        val shimmer = blackouts.isEmpty()
        LazyColumn(modifier = Modifier.padding(2.dp)){
            items(blackouts){ blackout ->
                BlackoutCard(blackout,textColor,shimmer)
            }
        }
    }

    @Composable
    fun BlackoutCard(blackout: blackoutModel,textColor:Color,shimmer:Boolean) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 0.dp)

                .fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(vertical=1.dp).fillMaxSize().placeholder(
                visible = shimmer,
                highlight = PlaceholderHighlight.shimmer(),
            ),horizontalArrangement = Arrangement.SpaceAround,verticalAlignment = Alignment.CenterVertically) {
                    Row(horizontalArrangement = Arrangement.SpaceAround,verticalAlignment = Alignment.CenterVertically) {
                        blackout.from?.let { Text(text = it, modifier = Modifier.padding(horizontal = 8.dp, vertical = 1.dp) ,style =  MaterialTheme.typography.h1, color = textColor) }
                        val timeAlias = blackout.from?.substring(0,1)?.let { getTimeAlias(it.toInt())}
                        //Text(text=  timeAlias.toString(),style=MaterialTheme.typography.h6)
                    }

                    Text(text = "to",style =  MaterialTheme.typography.h5, color = textColor)

                    Row(horizontalArrangement = Arrangement.SpaceAround,verticalAlignment = Alignment.CenterVertically) {
                        blackout.to?.let { Text(text = it, modifier = Modifier.padding(horizontal = 8.dp, vertical = 1.dp) ,style =  MaterialTheme.typography.h1, color = textColor) }
                    }

            }
        }

    }

fun getTimeAlias(time:Int):String{
    if(time<12){
        return "AM"
    }
    else{
        "PM"
    }
    return ""
}

class BlackoutModel(group:String) : ViewModel() {
        val blackoutCards = mutableStateListOf<blackoutModel>()

        init{
            getTodayBlackouts(group)
        }
        private fun getTodayBlackouts(group:String) {
            viewModelScope
                .launch {
                    val requestCall : Call<DayGroupSchedule>? = group?.let {
                        ServiceBuilder.api.getTodayGroupSchedule(
                            it
                        )
                    }
                    if (requestCall != null) {
                        requestCall.enqueue(object: Callback<DayGroupSchedule> {
                            override fun onResponse(
                                call: Call<DayGroupSchedule>,
                                response: Response<DayGroupSchedule>
                            ) {

                                val blackoutTime = response.body()?.time
                                if (blackoutTime != null) {
                                    blackoutCards.clear()
                                    blackoutCards.addAll(blackoutTime.times)

                                }
                            }
                            override fun onFailure(call: Call<DayGroupSchedule>, t: Throwable) {
                                //Toast.makeText(context, "an error occured while fetching today's schedule" , Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
        }
    }

class TomorrowBlackoutModel(group:String,context:Context) : ViewModel() {
    val blackoutCards = mutableStateListOf<blackoutModel>()

    init{
        getTomorrowBlackout(group,context)
    }
    private fun getTomorrowBlackout(group:String,context:Context) {
        viewModelScope
            .launch {
                val calendar: Calendar = Calendar.getInstance()
                val day: Int = calendar.get(Calendar.DAY_OF_WEEK)-1
                val requestCall : Call<DayGroupSchedule>? = ServiceBuilder.api.getDayGroupSchedule(day,group.uppercase())
                if (requestCall != null) {
                    requestCall.enqueue(object: Callback<DayGroupSchedule> {
                        override fun onResponse(
                            call: Call<DayGroupSchedule>,
                            response: Response<DayGroupSchedule>
                        ) {

                            val blackoutTime = response.body()?.time
                            if (blackoutTime != null) {
                                blackoutCards.clear()
                                blackoutCards.addAll(blackoutTime.times)

                            }
                        }
                        override fun onFailure(call: Call<DayGroupSchedule>, t: Throwable) {
                            Toast.makeText(context, "an error occured while fetching tomorrow schedule" , Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
    }
}

fun getGreeting():String{
    val now: LocalTime = LocalTime.now()

    if(now.hour in 6..12){
        return "Good morning"
    }
    else if(now.hour in 12..18){
        return "Good afternoon"
    }
    else if(now.hour in 18..23){
        return "Good evening"
    }
    return "Welcome back"
}
fun getGroupName(context: Context): String? {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    return sharedPreferences.getString("group", "")
}

fun getBlackoutState(blackoutTime:String):String{
    val now: LocalTime = LocalTime.now()
    val blackoutHour = blackoutTime.substring(0,2).toInt()
    val currentHour = now.hour

    if(currentHour >= blackoutHour && currentHour <= (blackoutHour+6) ){
        return "Blackout is underway"
    }
    else if(currentHour < blackoutHour){
        val remainingTime = blackoutHour-currentHour
        return "Blackout in $remainingTime hours"
    }
    else if(currentHour > (blackoutHour+6)){
        return "Blackout done already"
    }

    return ""
}






