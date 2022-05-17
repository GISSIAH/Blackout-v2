package com.example.bottomnavbardemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.bottomnavbardemo.screens.Welcome
import com.example.bottomnavbardemo.screens.getGroupSet
import com.example.bottomnavbardemo.ui.theme.BottomNavBarDemoTheme
import com.example.bottomnavbardemo.ui.theme.varBlue

class SplashScreen : ComponentActivity() {
    lateinit var context : Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            context = LocalContext.current

            if(getGroupSet(context) != ""){
                val intent = Intent(this@SplashScreen, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                val intent = Intent(this@SplashScreen, Welcome::class.java)
                startActivity(intent)
                finish()

            }



            BottomNavBarDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier=Modifier.fillMaxSize(),color = varBlue) {

                }
            }
        }
    }
}
