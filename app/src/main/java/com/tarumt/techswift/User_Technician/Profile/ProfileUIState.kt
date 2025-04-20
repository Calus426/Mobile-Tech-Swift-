package com.tarumt.techswift.User_Technician.Profile

import com.tarumt.techswift.Model.User

data class ProfileUiState(

    val oriProfile : User = User(),
    val updatedProfile: User = User(),
    val addressSuggestion: List<String> = emptyList(),
    val fullAddress: String = ""

)