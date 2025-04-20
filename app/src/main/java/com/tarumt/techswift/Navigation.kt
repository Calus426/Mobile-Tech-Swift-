package com.tarumt.techswift

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tarumt.techswift.Login_Signup.UiScreen.FlipCard
import com.tarumt.techswift.Login_Signup.ViewModel.AuthState
import com.tarumt.techswift.Login_Signup.ViewModel.AuthViewModel
import com.tarumt.techswift.Technician.TechnicianUI
import com.tarumt.techswift.User.UiScreen.History.UserHistoryUI
import com.tarumt.techswift.User.UiScreen.History.UserHistoryViewModel
import com.tarumt.techswift.User.UiScreen.Home.UserHomeUI
import com.tarumt.techswift.User_Technician.Profile.ProfileUI
import com.tarumt.techswift.User.UiScreen.ServiceDetails.ServiceDetailsUI
import com.tarumt.techswift.User.UiScreen.ServiceDetails.ServiceDetailsViewModel
import com.tarumt.techswift.User_Technician.Profile.ProfileViewModel


enum class Navigation(@StringRes val title: Int, val canNavigate: Boolean) {
    UserHome(title = R.string.home, canNavigate = false),
    UserHistory(title = R.string.history, canNavigate = false),
    ServiceDetails(title = R.string.service_details, canNavigate = true),
    Profile(title = R.string.profile, canNavigate = true),
    Login(title = R.string.login, canNavigate = false),
    TechnicianHome(title = R.string.technician_home, canNavigate = false),

}

@Composable
fun Navigate(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    serviceViewModel: ServiceDetailsViewModel = viewModel(),
    userHistoryViewModel: UserHistoryViewModel = viewModel(),
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel = viewModel()

) {
    val authState = authViewModel.authState.observeAsState()

    val role = authViewModel.role.observeAsState()

    val initialDestination = Navigation.Login.name


    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                val destination = when (role.value) {
                    "T" -> Navigation.TechnicianHome.name
                    "U" -> Navigation.UserHome.name
                    else -> Navigation.Login.name
                }

                navController.navigate(destination) {
                    popUpTo(0) { inclusive = true } // clear all previous screens
                }
            }

            is AuthState.Unauthenticated -> {
                navController.navigate(Navigation.Login.name) {
                    popUpTo(0) { inclusive = true } // clear all previous screens
                }
            }

            else -> Unit // Loading or null
        }
    }

    NavHost(
        navController = navController,
        startDestination = initialDestination,
        modifier = modifier
            .fillMaxSize()
    ) {
        composable(route = Navigation.Login.name) {
            FlipCard(
                authViewModel,
                onLoginButtonClick = {
                }

            )
        }

//            composable(route = Navigation.SignUp.name){
//                SignUpUI(
//                    authViewModel
//                )
//            }
        composable(route = Navigation.UserHome.name) {

            UserHomeUI(
                onServiceClick = {
                    serviceViewModel.resetServiceDetails()
                    serviceViewModel.updateServiceId(it)
                    navController.navigate(Navigation.ServiceDetails.name)
                }
            )
        }

        composable(route = Navigation.TechnicianHome.name) {

            TechnicianUI()
        }

        composable(route = Navigation.ServiceDetails.name) {
            ServiceDetailsUI(
                serviceViewModel,
                onSubmitRequestClicked = {
                    userHistoryViewModel.updateToastMessage(it)
                    navController.navigate(Navigation.UserHistory.name) {
                        // clears the entire back stack
                        popUpTo(0) // Clears all back stack
                    }
                }
            )
        }

        composable(route = Navigation.UserHistory.name) {
            UserHistoryUI(
                userHistoryViewModel
            )
        }

        composable(route = Navigation.Profile.name) {
            ProfileUI(
                profileViewModel = profileViewModel
            )
        }


    }
}
