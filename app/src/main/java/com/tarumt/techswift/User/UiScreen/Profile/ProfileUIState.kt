package com.tarumt.techswift.User.UiScreen.Profile

import com.tarumt.techswift.User.Model.User

data class ProfileUiState(

    val oriProfile : User = User(),
    val updatedProfile: User = User()

)