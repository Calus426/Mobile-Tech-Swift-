package com.tarumt.techswift.Technician

import com.tarumt.techswift.Model.Request

data class TechnicianUiState(
    val pendingList : List<Request> = emptyList(),
    val selectedTask: Request? = null,
    val showDialog: Boolean = false
)