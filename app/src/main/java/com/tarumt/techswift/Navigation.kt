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
import com.tarumt.techswift.Login_Signup.UiScreen.FlipCard
import com.tarumt.techswift.Login_Signup.ViewModel.AuthState
import com.tarumt.techswift.Login_Signup.ViewModel.AuthViewModel
import com.tarumt.techswift.Profile.ProfileUI
import com.tarumt.techswift.Profile.ProfileViewModel
import com.tarumt.techswift.Technician.TechnicianHistory.TechnicianHistoryUi
import com.tarumt.techswift.Technician.TechnicianHomeUI
import com.tarumt.techswift.User.History.UserHistoryUI
import com.tarumt.techswift.User.Home.UserHomeUI
import com.tarumt.techswift.User.Order.UserOrderUI
import com.tarumt.techswift.User.Order.UserOrderViewModel
import com.tarumt.techswift.User.ServiceDetails.ServiceDetailsUI
import com.tarumt.techswift.User.ServiceDetails.ServiceDetailsViewModel


enum class Navigation(@StringRes val title: Int, val canNavigate: Boolean) {
    UserHome(title = R.string.home, canNavigate = false),
    UserOrder(title = R.string.order, canNavigate = false),
    UserHistory(title = R.string.history, canNavigate = true),
    ServiceDetails(title = R.string.service_details, canNavigate = true),
    Profile(title = R.string.profile, canNavigate = true),
    Login(title = R.string.login, canNavigate = false),
    TechnicianHome(title = R.string.technician_home, canNavigate = false),
    TechnicianHistory(title = R.string.technician_history, canNavigate = false),

}

@Composable
fun Navigate(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    serviceViewModel: ServiceDetailsViewModel = viewModel(),
    userOrderViewModel: UserOrderViewModel = viewModel(),
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel = viewModel(),
    windowInfo: WindowInfo,
    navViewModel: NavViewModel = viewModel()

) {

    val authState = authViewModel.authState.observeAsState()

    val role = authViewModel.role.observeAsState()

    val initialDestination = Navigation.Login.name


    LaunchedEffect(authState.value) {

        when (authState.value) {
            is AuthState.Authenticated -> {
                if (!navViewModel.hasNavigated) {
                    val destination = when (role.value) {
                        "T" -> Navigation.TechnicianHome.name
                        "U" -> Navigation.UserHome.name
                        else -> Navigation.Login.name
                    }

                    navController.navigate(destination) {
                        popUpTo(0) { inclusive = true } // clear all previous screens
                    }
                    navViewModel.updateHasNavigated(true)
                }
            }


            is AuthState.Unauthenticated -> {
                navViewModel.updateHasNavigated(false)
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
                },
                windowInfo = windowInfo

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
                },
                windowInfo = windowInfo
            )
        }

        composable(route = Navigation.TechnicianHome.name) {
            TechnicianHomeUI(windowInfo = windowInfo)
        }

        composable(route = Navigation.TechnicianHistory.name) {
            TechnicianHistoryUi(windowInfo = windowInfo)
        }


        composable(route = Navigation.ServiceDetails.name) {
            ServiceDetailsUI(
                serviceViewModel,
                onSubmitRequestClicked = {
                    userOrderViewModel.updateToastMessage(it)
                    navController.navigate(Navigation.UserOrder.name) {
                        // clears the entire back stack
                        popUpTo(0) // Clears all back stack
                    }
                },
                windowInfo = windowInfo
            )
        }

        composable(route = Navigation.UserOrder.name) {
            UserOrderUI(
                userOrderViewModel,
                windowInfo = windowInfo
            )
        }

        composable(route = Navigation.UserHistory.name) {
            UserHistoryUI(windowInfo = windowInfo)
        }

        composable(route = Navigation.Profile.name) {
            ProfileUI(
                profileViewModel = profileViewModel
            )
        }


    }
}