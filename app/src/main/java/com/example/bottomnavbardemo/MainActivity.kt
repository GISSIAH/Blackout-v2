package com.example.bottomnavbardemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.bottomnavbardemo.ui.theme.BottomNavBarDemoTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BottomNavBarDemoTheme {
                MainScreen()
            }
        }
    }
}
