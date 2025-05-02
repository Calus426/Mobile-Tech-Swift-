package com.tarumt.techswift.Technician

import com.tarumt.techswift.Model.Request

data class TechnicianUiState(
    val pendingList : List<Request> = emptyList(),
    val selectedTask: Request? = null,
    val showDialog: Boolean = false,
    val userAddresses: Map<String, UserAddressInfo> = emptyMap()
)

data class UserAddressInfo(
    val fullAddress: String,
    val address1: String
)