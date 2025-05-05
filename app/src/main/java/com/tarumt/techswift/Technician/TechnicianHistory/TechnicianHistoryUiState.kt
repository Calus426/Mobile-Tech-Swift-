package com.tarumt.techswift.Technician.TechnicianHistory

import com.tarumt.techswift.Model.Request

data class TechnicianHistoryUiState(
    val inProgressTasks: List<Request> = emptyList(),
    val finishedTasks: List<Request> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

