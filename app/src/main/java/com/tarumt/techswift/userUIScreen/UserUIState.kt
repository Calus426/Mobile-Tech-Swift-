package com.tarumt.techswift.userUIScreen

import com.tarumt.techswift.userModel.Order

data class UserUIState(
    val inProgressList : List<Order> = emptyList(),
    val pendingList : List<Order> = emptyList()
)
