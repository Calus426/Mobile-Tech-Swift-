package com.tarumt.techswift.Login_Signup.UiScreen.SignUp

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tarumt.techswift.Model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignUpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUIState())
    val uiState : StateFlow<SignUpUIState> = _uiState.asStateFlow()

    var name by mutableStateOf("")
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

    var password by mutableStateOf("")
        private set

    fun nameUpdate(it: String) {
        name = it
    }

    fun addressUpdate(it: String) {
        address = it
    }

    fun emailUpdate(it: String) {
        email = it
    }

    fun phoneUpdate(it: String) {
        phone = it
    }

    fun genderUpdate(it: String) {
        gender = it
    }

    fun postcodeUpdate(it: String) {
        postcode = it
    }

    fun stateUpdate(it: String) {
        state = it
    }
    fun passwordUpdate(it: String) {
        password = it
    }

    fun updateFullAdress(fullAddress: String) {
        _uiState.update { currentState ->
            currentState.copy(
                fullAddress = fullAddress
            )
        }
    }

    fun updateProfileDetails(context: Context) {

        //update the current profile value.
        _uiState.update { currentState ->
            currentState.copy(
                currentProfile = User(
                    name = name,
                    email = email,
                    phone = phone,
                    gender = gender,
                    address1 = address,
                    postcode = postcode,
                    state = state,
                    fullAddress = _uiState.value.fullAddress
                )
            )
        }
    }
}