package com.tarumt.techswift.User.Order

import com.tarumt.techswift.Model.Request

data class UserOrderUiState(
    val inProgressList: List<RequestDTO> = emptyList(),
    val pendingList: List<Request> = emptyList(),
    val count : Int = 0,
    val toastMessage: String = "",
    val technicianName : String = "",
    val technicianPhone : String = "",
    val statusScreen : String = "inProgress"
)

data class RequestDTO(
    val request: Request = Request(),
    val technicianName: String = "",
    val technicianPhone: String = ""
)