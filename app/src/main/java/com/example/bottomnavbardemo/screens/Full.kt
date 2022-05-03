package com.example.bottomnavbardemo.screens


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
import com.example.bottomnavbardemo.ui.theme.Red
import com.example.bottomnavbardemo.ui.theme.lightRed
import com.example.loadshedding.models.DayGroupSchedule
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


@Composable
fun FullScreen() {
    Column(
        modifier = Modifier
            .padding(horizontal = 2.dp, vertical = 50.dp)

    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),horizontalArrangement = Arrangement.Center) {
            Text(text = "This week",style = MaterialTheme.typography.h2)
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
    val calendar: Calendar = Calendar.getInstance()
    val day: Int = calendar.get(Calendar.DAY_OF_WEEK)-2
    val option1 = WeekDay(day,getDayName(day))
    var selectedOption by remember { mutableStateOf(option1)}
    Column() {
        LazyRow(modifier = Modifier
            .padding(horizontal = 8.dp)
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






