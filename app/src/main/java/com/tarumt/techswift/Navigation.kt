package com.tarumt.techswift

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tarumt.techswift.User.UiScreen.History.UserHistoryUI
import com.tarumt.techswift.User.UiScreen.Home.UserHomeUI


enum class Navigation(){
    Home,
    History
}

@Composable
fun Navigate(navController: NavHostController = rememberNavController(),
             modifier : Modifier = Modifier){
    NavHost(
        navController = navController,
        startDestination = Navigation.Home.name,
        modifier = modifier
            .fillMaxSize()
    ){
        composable(route = Navigation.Home.name){
            UserHomeUI()
        }

        composable(route = Navigation.History.name){
            UserHistoryUI()
        }
    }

}