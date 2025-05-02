package com.tarumt.techswift.Profile

import android.net.Uri
import com.tarumt.techswift.Model.User

data class ProfileUiState(

    val oriProfile : User = User(),
    val updatedProfile: User = User(),
    val addressSuggestion: List<String> = emptyList(),
    val fullAddress: String = "",
    val selectedImageUri : Uri? = null

)