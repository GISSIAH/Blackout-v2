package com.example.bottomnavbardemo.screens

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bottomnavbardemo.MainScreen
import com.example.bottomnavbardemo.api.ServiceBuilder
import com.example.bottomnavbardemo.models.blackoutModel
import com.example.bottomnavbardemo.ui.theme.BottomNavBarDemoTheme
import com.example.bottomnavbardemo.ui.theme.Green
import com.example.bottomnavbardemo.ui.theme.Red
import com.example.bottomnavbardemo.ui.theme.ShimmerColorShades
import com.example.loadshedding.models.todayGroupSchedule
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime




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
        val group = getGroupName(context)
        if (group != null) {
            Column{
                val textPaddingModifier  = Modifier.padding(10.dp)
                Text(text = getGreeting(),
                    modifier = textPaddingModifier,
                    style= MaterialTheme.typography.h1
                )
                Text(text = "Today's Schedule",
                    modifier = textPaddingModifier,
                    style =  MaterialTheme.typography.h3
                )
                BlackoutListScreen(viewModel = BlackoutModel(group))
                /*
                LazyColumn {

                    /**
                    Lay down the Shimmer Animated item 5 time
                    [repeat] is like a loop which executes the body
                    according to the number specified
                     */
                    repeat(5) {
                        item {
                            ShimmerAnimation()

                        }
                    }
                }
                 */

            }
        }
    }

    @Composable
    fun BlackoutListScreen(
        // pass the view model in this form for convenient testing
        viewModel: BlackoutModel
    ) {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            BlackoutList(viewModel.blackoutCards)
        }
    }

    @Composable
    fun BlackoutList(blackouts: SnapshotStateList<blackoutModel>) {
        LazyColumn(modifier = Modifier.padding(2.dp)){
            items(blackouts){ blackout ->
                BlackoutCard(blackout)
            }
        }
    }

    @Composable
    fun BlackoutCard(blackout: blackoutModel) {
        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth()
                .height(70.dp),
            elevation = 2.dp,
            backgroundColor = Color.White,
            shape = RoundedCornerShape(corner = CornerSize(10.dp))
        ) {
            Row(modifier = Modifier.fillMaxSize(),horizontalArrangement = Arrangement.SpaceAround,verticalAlignment = Alignment.CenterVertically) {
                Card(
                    modifier = Modifier.height(55.dp),
                    elevation = 4.dp,
                    backgroundColor = Red
                ){
                    Row(horizontalArrangement = Arrangement.SpaceAround,verticalAlignment = Alignment.CenterVertically) {
                        blackout.from?.let { Text(text = it, modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp) ,style =  MaterialTheme.typography.h4) }
                    }
                }
                Text(text = "to",style =  MaterialTheme.typography.h4)
                Card(
                    modifier = Modifier.height(55.dp),
                    elevation = 4.dp,
                    backgroundColor = Green
                ){
                    Row(horizontalArrangement = Arrangement.SpaceAround,verticalAlignment = Alignment.CenterVertically) {
                        blackout.to?.let { Text(text = it, modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp) ,style =  MaterialTheme.typography.h4) }
                    }

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
                    val requestCall : Call<todayGroupSchedule>? = group?.let {
                        ServiceBuilder.api.getTodayGroupSchedule(
                            it
                        )
                    }
                    if (requestCall != null) {
                        requestCall.enqueue(object: Callback<todayGroupSchedule> {
                            override fun onResponse(
                                call: Call<todayGroupSchedule>,
                                response: Response<todayGroupSchedule>
                            ) {

                                val blackoutTime = response.body()?.time
                                if (blackoutTime != null) {
                                    blackoutCards.clear()
                                    blackoutCards.addAll(blackoutTime.times)

                                }
                            }
                            override fun onFailure(call: Call<todayGroupSchedule>, t: Throwable) {
                                //Toast.makeText(context, "an error occured while fetching today's schedule" , Toast.LENGTH_SHORT).show()
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





