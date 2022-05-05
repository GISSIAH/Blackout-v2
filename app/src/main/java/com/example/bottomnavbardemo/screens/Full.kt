package com.example.bottomnavbardemo.screens


import android.content.Context
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bottomnavbardemo.api.ServiceBuilder
import com.example.bottomnavbardemo.models.WeekDay
import com.example.bottomnavbardemo.models.blackoutModel
import com.example.bottomnavbardemo.ui.theme.Gray500
import com.example.bottomnavbardemo.ui.theme.Gray700
import com.example.bottomnavbardemo.ui.theme.Red
import com.example.bottomnavbardemo.ui.theme.lightRed
import com.example.loadshedding.models.DayGroupSchedule
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import org.joda.time.DateTime
import java.text.DateFormatSymbols


@Composable
fun FullScreen() {
    val cal = Calendar.getInstance()
    val year = cal.get(Calendar.YEAR)
    val week_no = cal.get(Calendar.WEEK_OF_YEAR)

    val week_range = getWeekRange(year,week_no)
    val low_date = "${week_range?.first?.dayOfMonth} ${DateFormatSymbols().getMonths()[week_range?.first?.monthOfYear?.minus(1)!!]}"

    val high_date = "${week_range?.second?.dayOfMonth} ${DateFormatSymbols().getMonths()[week_range?.second?.monthOfYear?.minus(1)!!]}"
    Column(
        modifier = Modifier
            .padding(horizontal = 2.dp, vertical = 35.dp)

    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),horizontalArrangement = Arrangement.Center) {
            Text(text = "This week",style = MaterialTheme.typography.h2)
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),horizontalArrangement = Arrangement.Center) {
            if (week_range != null) {
                Text(text = "${low_date} to ${high_date}",style = MaterialTheme.typography.h5,color = Gray700)
            }
        }

        WeekListScreen()



    }
}
@Composable
@Preview
fun FullScreenPreview() {
    Column {
        Text(text = "This week",style = MaterialTheme.typography.h2)
        WeekListScreen()
    }
}



@Composable
fun WeekListScreen() {

    val weekList = listOf<WeekDay>(
        WeekDay(0,"MON"),
        WeekDay(1,"TUE"),
        WeekDay(2,"WED"),
        WeekDay(3,"THU"),
        WeekDay(4,"FRI"),
        WeekDay(5,"SAT"),
        WeekDay(6,"SUN"),
    )

    Surface() {
        WeekList(weekList)
    }
}




@Composable
fun WeekList(weekList:List<WeekDay>) {

    val context = LocalContext.current
    val group = getGroupName(context)
    val calendar: Calendar = Calendar.getInstance()
    val day: Int = calendar.get(Calendar.DAY_OF_WEEK)-2
    val option1 = WeekDay(day,getDayName(day))
    var selectedOption by remember { mutableStateOf(option1)}
    Column() {
        LazyRow(modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 10.dp)
            .fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween){
            items(weekList){ weekDay ->
                Surface(modifier = Modifier.selectable(
                    selected = true,
                    onClick = {
                        selectedOption = weekDay
                    }
                )) {
                    if(weekDay == selectedOption){
                        WeekDayCard(weekDay,true)
                    }else{
                        WeekDayCard(weekDay,false)
                    }
                }
            }
        }
    Column(modifier = Modifier.padding(vertical = 20.dp)) {
        group?.let { DayScheduleModel(it,selectedOption.day) }?.let { DayBlackoutScreen(viewModel = it) }
    }


    }

}

@Composable
fun WeekDayCard(weekDay:WeekDay,selected:Boolean) {
    Card(
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .height(40.dp)
        ,
        elevation = 4.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(corner = CornerSize(5.dp))

    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 2.dp, vertical = 1.dp),
            verticalArrangement = Arrangement.Center,horizontalAlignment = Alignment.CenterHorizontally

        ) {
            if(selected){
                Text(text = weekDay.dayName,style = MaterialTheme.typography.h6,color = Red,fontWeight = FontWeight.Bold)

            }else{
                Text(text = weekDay.dayName,style = MaterialTheme.typography.h6)
            }

        }
    }


}

fun getDayName(day: Int): String {
    return when (day) {
        0 -> return "MON"
        1 -> return "TUE"
        2 -> return "WED"
        3 -> return "THU"
        4 -> return "FRI"
        5 -> return "SAT"
        6 -> return "SUN"
        else -> ""
    }
}



class DayScheduleModel(group:String,day:Int) : ViewModel() {
    val blackoutCards = mutableStateListOf<blackoutModel>()

    init{
        getDayBlackouts(day,group)
    }
    private fun getDayBlackouts(day:Int,group:String) {
        viewModelScope
            .launch {
                val requestCall : Call<DayGroupSchedule>? = ServiceBuilder.api.getDayGroupSchedule(day,group.uppercase())
                if (requestCall != null) {
                    requestCall.enqueue(object: Callback<DayGroupSchedule> {
                        override fun onResponse(
                            call: Call<DayGroupSchedule>,
                            response: Response<DayGroupSchedule>
                        ) {

                            val blackoutTime = response.body()?.time
                            //Toast.makeText(context, response.body().toString() , Toast.LENGTH_SHORT).show()
                            if (blackoutTime != null) {
                                blackoutCards.clear()
                                blackoutCards.addAll(blackoutTime.times)

                            }
                        }
                        override fun onFailure(call: Call<DayGroupSchedule>, t: Throwable) {
                            //Toast.makeText(context, "an error occured while fetching tomorrow schedule" , Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
    }
}

@Composable
fun DayBlackoutScreen(
    // pass the view model in this form for convenient testing
    viewModel: DayScheduleModel
) {
    val context = LocalContext.current

    //Toast.makeText(context, day.toString(), Toast.LENGTH_SHORT).show()
    // A surface container using the 'background' color from the theme
    Surface() {
        DayScheduleList(viewModel.blackoutCards)
    }
}




@Composable
fun DayScheduleList(blackouts: SnapshotStateList<blackoutModel>) {
    LazyColumn(modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth()
        .fillMaxHeight()){
        items(blackouts){ blackout ->
            DayCard(blackout)
        }
    }
}


@Composable
fun DayCard(blackout:blackoutModel){
    Card(
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 25.dp)
            .fillMaxWidth()
            .height(100.dp)

        ,
        elevation = 8.dp,
        shape = RoundedCornerShape(corner = CornerSize(4.dp))
    ) {
        Row(modifier = Modifier.padding(horizontal = 5.dp),horizontalArrangement = Arrangement.SpaceAround,verticalAlignment = Alignment.CenterVertically) {
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                Text(text = "From",style = MaterialTheme.typography.body1)
                blackout.from?.let { Text(text = it,style = MaterialTheme.typography.h5) }
            }
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                Text(text = "To",style = MaterialTheme.typography.body1)
                blackout.to?.let { Text(text = it,style = MaterialTheme.typography.h5) }
            }
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Duration",style = MaterialTheme.typography.body1)
                Row(verticalAlignment = Alignment.Bottom) {
                    blackout.duration?.let { Text(text = "${it.toString()}",style = MaterialTheme.typography.h5) }
                    Text(text="hours",style = MaterialTheme.typography.subtitle1,modifier = Modifier.padding(horizontal = 5.dp))
                }

            }

        }
    }
}


fun getWeekRange(year:Int,week_no: Int): Pair<DateTime?, DateTime?>? {
    val startOfWeek = DateTime().withYear(year).withWeekOfWeekyear(week_no).withDayOfWeek(1)
    val endOfWeek = startOfWeek.plusDays(6)
    return Pair(startOfWeek, endOfWeek)
}