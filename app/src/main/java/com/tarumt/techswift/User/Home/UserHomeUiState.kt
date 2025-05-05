package com.tarumt.techswift.User.Home

import com.tarumt.techswift.Model.Service

data class UserHomeUiState(
    val listOfService : List<Service> = listOf<Service>()
)