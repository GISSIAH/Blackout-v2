package com.example.bottomnavbardemo.screens


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bottomnavbardemo.api.ServiceBuilder
import com.example.bottomnavbardemo.models.blackoutModel
import com.example.bottomnavbardemo.ui.theme.Gray700
import com.example.bottomnavbardemo.ui.theme.Red
import com.example.bottomnavbardemo.viewModels.SearchViewModel
import com.example.loadshedding.models.DayGroupSchedule
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


@Composable
fun MapScreen() {
        Box(
            modifier = Modifier.fillMaxSize(),
        ){
            TopBar(searchViewModel = SearchViewModel() )

        }

    }


@Composable
fun TopBar(searchViewModel:SearchViewModel){
    //val searchWidgetState by searchViewModel.searchWidgetState
    val searchTextState by searchViewModel.searchTextState
    val context = LocalContext.current
    Column(modifier=Modifier.fillMaxWidth()) {
        SearchBar(
            text = searchTextState,
            onTextChange ={
                          searchViewModel.updateSearchTextState(it)
            },
            onCloseClicked = {
                             searchViewModel.updateSearchTextState("")
            },
            onSearchClicked = {

                //Log.d("Search clicked",it)


            }

        )

        Column(modifier = Modifier.padding(vertical = 25.dp)) {
            AreaBlackoutScreen(viewModel = SearchModel(searchTextState))
        }
    }

}

@Composable
fun SearchBar(
    text:String,
    onTextChange: (String)-> Unit,
    onCloseClicked : ()-> Unit,
    onSearchClicked : (String) -> Unit
){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(56.dp),
        elevation = AppBarDefaults.TopAppBarElevation,
        //color = Color.White
    ) {
        TextField(modifier = Modifier.fillMaxWidth()
            ,value=text,
            onValueChange = {
                onTextChange(it)
            },
            placeholder = {
                Text(
                    modifier = Modifier.alpha((ContentAlpha.medium))
                    ,text = "Search Area..."
                )
            },
            textStyle = TextStyle(fontSize = MaterialTheme.typography.subtitle1.fontSize),
            singleLine = true,
            leadingIcon = {
                IconButton(
                    modifier=Modifier.alpha(ContentAlpha.medium),
                    onClick = {onSearchClicked(text)}) {
                    Icon(imageVector = Icons.Outlined.Search,contentDescription = "Search Icon",tint= Red)
                }
            },
            trailingIcon = {
                IconButton(
                    modifier=Modifier.alpha(ContentAlpha.medium),
                    onClick = {
                        if(text.isNotEmpty()){
                            onTextChange("")
                        }else{
                            onCloseClicked()
                        }
                    }) {
                    Icon(imageVector = Icons.Outlined.Close,contentDescription = "Close Icon",tint= Gray700)
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                }
            )
            )

    }

}

@Composable
@Preview
fun SearchBarPreview(){
    SearchBar(
        text = "Some Random",
        onTextChange ={},
        onCloseClicked = {},
        onSearchClicked = {}

    )
}

class SearchModel(area:String) : ViewModel() {
    val blackoutCards = mutableStateListOf<blackoutModel>()

    init{
        getAreaBlackoutSchedule(area)
    }
    private fun getAreaBlackoutSchedule(area:String) {
        viewModelScope
            .launch {
                val calendar: Calendar = Calendar.getInstance()
                val day: Int = calendar.get(Calendar.DAY_OF_WEEK)-2
                val requestCall : Call<DayGroupSchedule>? = ServiceBuilder.api.getAreaSchedule(area,day)
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

@Composable
fun AreaBlackoutScreen(
    // pass the view model in this form for convenient testing
    viewModel: SearchModel
) {
    val context = LocalContext.current

    //Toast.makeText(context, day.toString(), Toast.LENGTH_SHORT).show()
    // A surface container using the 'background' color from the theme
    //Toast.makeText(context, "${viewModel.blackoutCards.}", Toast.LENGTH_SHORT).show()
    if(viewModel.blackoutCards.isEmpty()){
        Row(modifier = Modifier.fillMaxSize(),horizontalArrangement = Arrangement.Center,verticalAlignment = Alignment.CenterVertically) {
            Text(text = "No search results found.", style = MaterialTheme.typography.h5)
        }

    }else{
            AreaScheduleList(viewModel.blackoutCards)
    }

}




@Composable
fun AreaScheduleList(blackouts: SnapshotStateList<blackoutModel>) {
    Column{
        Text(text="Today",modifier = Modifier.padding(horizontal = 5.dp),style = MaterialTheme.typography.h3)
        LazyColumn(modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .fillMaxHeight()){
            items(blackouts){ blackout ->
                DayCard(blackout)
            }
        }
    }

}
