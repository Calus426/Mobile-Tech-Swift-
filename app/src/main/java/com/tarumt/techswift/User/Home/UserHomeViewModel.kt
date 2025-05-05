package com.tarumt.techswift.User.Home

import androidx.lifecycle.ViewModel
import com.tarumt.techswift.User.Datasource.ServiceDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserHomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UserHomeUiState())
    val uiState : StateFlow<UserHomeUiState> = _uiState.asStateFlow()

    private fun loadServicesOptions()
    {
        _uiState.update{ currentState ->

            currentState.copy(
                listOfService = ServiceDataSource().loadServices()
            )

        }
    }
    init{
        loadServicesOptions()
    }
}