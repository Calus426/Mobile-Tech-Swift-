package com.tarumt.techswift.User.UiScreen.History

import com.tarumt.techswift.Model.Request
import com.tarumt.techswift.Model.Service


data class UserHistoryUiState(
    val finishedList : List<Request> = emptyList(),
    val technicianName : String = ""
)