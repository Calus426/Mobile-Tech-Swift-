package com.tarumt.techswift.Technician

import com.tarumt.techswift.Model.Request

data class TechnicianUiState(
//    val taskId: String = "",
//    val serviceName: String,
//    val price: String,
//    val address: String,

    val pendingList : List<Request> = emptyList()
)