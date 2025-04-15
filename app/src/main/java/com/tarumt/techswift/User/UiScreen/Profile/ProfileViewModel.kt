package com.tarumt.techswift.User.UiScreen.Profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {

   private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState : StateFlow<ProfileUiState> = _uiState.asStateFlow()

    var name by mutableStateOf("Gem")
        private set

    var email by mutableStateOf("")
        private set

    var phone by mutableStateOf("")
        private set

    var gender by mutableStateOf("")
        private set

    var address by mutableStateOf("")
        private set

    var postcode by mutableStateOf("")
        private set

    var state by mutableStateOf("")
        private set


    fun nameUpdate(it : String){
        name = it
    }

    fun addressUpdate(it: String){
        address = it
    }

    fun emailUpdate(it: String){
        email = it
    }

    fun phoneUpdate(it: String){
        phone = it
    }

    fun genderUpdate(it: String){
        gender = it
    }

    fun postcodeUpdate(it: String){
        postcode = it
    }

    fun stateUpdate(it: String){
        state = it
    }

}