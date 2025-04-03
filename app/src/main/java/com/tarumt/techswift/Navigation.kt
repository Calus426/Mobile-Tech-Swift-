package com.tarumt.techswift

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tarumt.techswift.User.UiScreen.History.UserHistoryUI
import com.tarumt.techswift.User.UiScreen.Home.UserHomeUI
import com.tarumt.techswift.User.UiScreen.ServiceDetails.ServiceDetailsUI
import com.tarumt.techswift.User.UiScreen.ServiceDetails.ServiceDetailsViewModel


enum class Navigation(){
    Home,
    History,
    ServiceDetails
}

@Composable
fun Navigate(navController: NavHostController = rememberNavController(),
             modifier : Modifier = Modifier){
    val serviceViewModel : ServiceDetailsViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = Navigation.Home.name,
        modifier = modifier
            .fillMaxSize()
    ){
        composable(route = Navigation.Home.name){

            UserHomeUI(
                onServiceClick = {
                    serviceViewModel.resetServiceDetails()
                    serviceViewModel.updateServiceId(it)
                    navController.navigate(Navigation.ServiceDetails.name)}
            )
        }

        composable(route = Navigation.ServiceDetails.name){
            ServiceDetailsUI(
                serviceViewModel,
                onSubmitRequestClicked = {
                    serviceViewModel.updateTextDescription()
                    serviceViewModel.saveServiceRequest()
                    navController.navigate(Navigation.History.name) {
                        // clears the entire back stack
                        popUpTo(0) // Clears all back stack
                    }
                }
            )
        }

        composable(route = Navigation.History.name){
            UserHistoryUI()
        }

    }

}