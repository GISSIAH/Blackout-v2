package com.example.bottomnavbardemo.screens

import android.content.ContentValues.TAG
import android.content.Context
import android.preference.PreferenceManager
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bottomnavbardemo.R
import com.example.bottomnavbardemo.api.ServiceBuilder
import com.example.bottomnavbardemo.models.blackoutModel
import com.example.bottomnavbardemo.ui.theme.Red
import com.example.bottomnavbardemo.ui.theme.ShimmerColorShades
import com.example.bottomnavbardemo.ui.theme.lightRed
import com.example.loadshedding.models.DayGroupSchedule
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime
import java.util.*

fun getToken(){

}
@Composable
    fun HomeScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopSection()
        }
    }
    @Composable
    @Preview
    fun HomeScreenPreview() {
        TopSection()
    }
    @Composable
    fun TopSection(){
        val context = LocalContext.current
        val group =  getGroupName(context)

        if (group != null) {
            Column(modifier = Modifier.padding(vertical = 20.dp)){
                val textPaddingModifier  = Modifier.padding(5.dp)
                Text(text = getGreeting(),
                    modifier = textPaddingModifier,
                    style= MaterialTheme.typography.h1
                )

                Row(modifier = Modifier
                    .fillMaxWidth(5f)
                    .padding(horizontal = 5.dp, vertical = 20.dp)) {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 5.dp)
                            .height(120.dp)
                            .weight(2f)
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
                            BlackoutListScreen(viewModel = BlackoutModel(group))
                        }

                    }
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 5.dp)
                            .height(120.dp)
                            .weight(2f)

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
    fun BlackoutListScreen(
        // pass the view model in this form for convenient testing
        viewModel: BlackoutModel
    ) {
        val context = LocalContext.current

        //Toast.makeText(context, day.toString(), Toast.LENGTH_SHORT).show()
        // A surface container using the 'background' color from the theme
        Surface(color = Red) {
            BlackoutList(viewModel.blackoutCards,Color.White)
        }
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
        LazyColumn(modifier = Modifier.padding(2.dp)){
            items(blackouts){ blackout ->
                BlackoutCard(blackout,textColor)
            }
        }
    }

    @Composable
    fun BlackoutCard(blackout: blackoutModel,textColor:Color) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 0.dp)
                .fillMaxWidth()
        ) {
            Row(modifier = Modifier.fillMaxSize(),horizontalArrangement = Arrangement.SpaceAround,verticalAlignment = Alignment.CenterVertically) {
                    Row(horizontalArrangement = Arrangement.SpaceAround,verticalAlignment = Alignment.CenterVertically) {
                        blackout.from?.let { Text(text = it, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp) ,style =  MaterialTheme.typography.h5, color = textColor) }
                    }

                    Text(text = "to",style =  MaterialTheme.typography.h5, color = textColor)

                    Row(horizontalArrangement = Arrangement.SpaceAround,verticalAlignment = Alignment.CenterVertically) {
                        blackout.to?.let { Text(text = it, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp) ,style =  MaterialTheme.typography.h5, color = textColor) }
                    }

            }
        }

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
                Toast.makeText(context, day.toString() , Toast.LENGTH_SHORT).show()
                val requestCall : Call<DayGroupSchedule>? = ServiceBuilder.api.getDayGroupSchedule(day,group.uppercase())
                if (requestCall != null) {
                    requestCall.enqueue(object: Callback<DayGroupSchedule> {
                        override fun onResponse(
                            call: Call<DayGroupSchedule>,
                            response: Response<DayGroupSchedule>
                        ) {

                            val blackoutTime = response.body()?.time
                            Toast.makeText(context, response.body().toString() , Toast.LENGTH_SHORT).show()
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
        return "Good Morning"
    }
    else if(now.hour in 12..18){
        return "Good Afternoon"
    }
    else if(now.hour in 18..23){
        return "Good Evening"
    }
    return "Welcome Back"
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

@Composable
fun ShimmerItem(
    brush: Brush
) {
    // Column composable containing spacer shaped like a rectangle,
    // set the [background]'s [brush] with the brush receiving from [ShimmerAnimation]
    // Composable which is the Animation you are gonna create.
    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(250.dp)
                .background(brush = brush)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(vertical = 8.dp)
                .background(brush = brush)
        )
    }
}


@Composable
fun ShimmerAnimation(
) {

    /*
     Create InfiniteTransition
     which holds child animation like [Transition]
     animations start running as soon as they enter
     the composition and do not stop unless they are removed
    */
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        /*
         Specify animation positions,
         initial Values 0F means it
         starts from 0 position
        */
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(


            // Tween Animates between values over specified [durationMillis]
            tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        )
    )

    /*
      Create a gradient using the list of colors
      Use Linear Gradient for animating in any direction according to requirement
      start=specifies the position to start with in cartesian like system Offset(10f,10f) means x(10,0) , y(0,10)
      end = Animate the end position to give the shimmer effect using the transition created above
    */
    val brush = Brush.linearGradient(
        colors = ShimmerColorShades,
        start = Offset(10f, 10f),
        end = Offset(translateAnim, translateAnim)
    )

    ShimmerItem(brush = brush)
}





