package com.tarumt.techswift.User.UiScreen.History

import com.tarumt.techswift.User.Model.Order

data class UserUIState(
    val inProgressList : List<Order> = emptyList(),
    val pendingList : List<Order> = emptyList()
)
