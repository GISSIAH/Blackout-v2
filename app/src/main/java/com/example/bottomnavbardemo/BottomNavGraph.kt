package com.example.bottomnavbardemo

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bottomnavbardemo.navigation.AllScreens
import com.example.bottomnavbardemo.screens.*

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(route = BottomBarScreen.Home.route) {
            HomeScreen(navController)
        }
        composable(route = BottomBarScreen.Full.route) {
            FullScreen()
        }
        composable(route = BottomBarScreen.Map.route) {
            MapScreen()
        }
        composable(route = BottomBarScreen.Settings.route) {
            SettingsScreen()
        }
        composable(route = AllScreens.Notifications.route) {
            NotificationScreen(navController)
        }

    }
}