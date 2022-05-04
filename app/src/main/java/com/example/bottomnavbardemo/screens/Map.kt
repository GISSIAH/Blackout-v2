package com.example.bottomnavbardemo.screens


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bottomnavbardemo.ui.theme.Gray700
import com.example.bottomnavbardemo.ui.theme.Red
import com.example.bottomnavbardemo.viewModels.SearchViewModel


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
    val searchWidgetState by searchViewModel.searchWidgetState
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
                Toast.makeText(context, "Searched $it", Toast.LENGTH_SHORT).show()
                Log.d("Search clicked",it)
            }

        )
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
        color = Color.White
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