package com.tarumt.techswift

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController

class NavViewModel : ViewModel() {
    var navController: NavHostController? = null

    var hasNavigated by mutableStateOf(false)
        private set


    fun updateHasNavigated(update : Boolean){
        hasNavigated = update
    }
}