package com.tarumt.techswift.User.UiScreen.Order

import com.tarumt.techswift.Model.Request

data class UserOrderUiState(
    val inProgressList: List<Request> = emptyList(),
    val pendingList: List<Request> = emptyList(),
    val count : Int = 0,
    val toastMessage: String = "",
    val technicianName : String = "",
    val technicianPhone : String = ""
)