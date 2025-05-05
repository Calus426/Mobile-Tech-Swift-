package com.tarumt.techswift.User.History

import com.tarumt.techswift.Model.Request


data class UserHistoryUiState(
    val finishedList : List<RequestDTO> = emptyList(),
)


data class RequestDTO(
    val request: Request = Request(),
    val technicianName: String = ""
)
