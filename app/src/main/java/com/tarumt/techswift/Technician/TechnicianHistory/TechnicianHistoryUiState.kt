package com.tarumt.techswift.Technician.TechnicianHistory

data class HistoryTaskUi(
    val id: String = "",
    val serviceName: String = "",
    val price: String = "",
    val technicianName: String = ""
)

// Full UI State for the History screen
data class TechnicianHistoryUiState(
    val inProgressTasks: List<HistoryTaskUi> = emptyList(),
    val finishedTasks: List<HistoryTaskUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class HistoryTask(
    val serviceName: String = "",
    val price: String = "",
    val status: String = "",
    val technicianName: String = ""
)