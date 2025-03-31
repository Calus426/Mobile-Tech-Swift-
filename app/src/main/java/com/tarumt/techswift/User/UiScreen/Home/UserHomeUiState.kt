package com.tarumt.techswift.User.UiScreen.Home

import com.tarumt.techswift.User.Model.Service

data class UserHomeUiState(
    val listOfService : List<Service> = listOf<Service>()
)