package com.tarumt.techswift

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tarumt.techswift.userUIScreen.UserHistoryUI
import com.tarumt.techswift.userUIScreen.UserHomeUI


enum class Navigation(){
    Home,
    History
}

@Composable
fun Navigate(navController: NavHostController = rememberNavController()){

    Scaffold(
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Navigation.Home.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            composable(route = Navigation.Home.name){
                UserHomeUI(
                    onNextButtonClicked = {
                        navController.navigate(Navigation.History.name)
                    }
                )
            }

            composable(route = Navigation.History.name){
                UserHistoryUI(
                    onNextButtonClicked = {navController.navigateUp()}
                )
            }
        }
    }
}