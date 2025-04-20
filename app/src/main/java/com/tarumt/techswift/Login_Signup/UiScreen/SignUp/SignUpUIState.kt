package com.tarumt.techswift.Login_Signup.UiScreen.SignUp

import com.tarumt.techswift.Model.User

data class SignUpUIState (
    val currentProfile : User = User(),
    val addressSuggestion: List<String> = emptyList(),
    val fullAddress: String = "",
    )