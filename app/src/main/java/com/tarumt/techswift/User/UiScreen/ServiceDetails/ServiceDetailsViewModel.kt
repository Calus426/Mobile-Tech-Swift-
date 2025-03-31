package com.tarumt.techswift.User.UiScreen.ServiceDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ServiceDetailsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ServiceDetailsUiState())
    val uiState : StateFlow<ServiceDetailsUiState> = _uiState.asStateFlow()
    var userDescription by mutableStateOf("")
        private set

    fun descriptionUpdate(description : String){
        userDescription = description
    }

    fun updateServiceId(id : Int){

        _uiState.update { currentState ->
            currentState.copy(
                serviceId = id
            )
        }
    }
}