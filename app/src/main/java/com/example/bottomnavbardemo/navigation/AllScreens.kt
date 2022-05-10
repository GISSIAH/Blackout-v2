package com.example.bottomnavbardemo.navigation

sealed class AllScreens(val route:String){
    object Home:AllScreens(route="home")
    object Notifications:AllScreens(route="notifications")
}
