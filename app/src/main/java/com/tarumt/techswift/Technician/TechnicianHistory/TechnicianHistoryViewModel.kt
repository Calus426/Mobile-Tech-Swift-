package com.tarumt.techswift.Technician.TechnicianHistory

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.tarumt.techswift.Technician.TechnicianUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TechnicianHistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TechnicianHistoryUiState())
    val uiState: StateFlow<TechnicianHistoryUiState> = _uiState.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    fun fetchHistory() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        db.collection("requests")
            .whereEqualTo("pending", false) // Only accepted tasks
            .get()
            .addOnSuccessListener { result ->
                val inProgress = mutableListOf<HistoryTaskUi>()
                val finished = mutableListOf<HistoryTaskUi>()

                result.forEach { doc ->
                    val task = doc.toObject(HistoryTask::class.java)
                    val uiTask = HistoryTaskUi(
                        id = doc.id,
                        serviceName = task.serviceName,
                        price = task.price,
                        technicianName = task.technicianName
                    )
                    if (task.status == "inProgress") {
                        inProgress.add(uiTask)
                    } else if (task.status == "finished") {
                        finished.add(uiTask)
                    }
                }

                _uiState.value = TechnicianHistoryUiState(
                    inProgressTasks = inProgress,
                    finishedTasks = finished,
                    isLoading = false
                )
            }
            .addOnFailureListener { e ->
                _uiState.value = TechnicianHistoryUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown Error"
                )
            }
    }
    fun markTaskAsFinished(taskId: String) {
        db.collection("Technician")
            .document(taskId)
            .update("status", "finished")
            .addOnSuccessListener {
                fetchHistory() // Refresh the list after marking done
            }
            .addOnFailureListener { e ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error updating task"
                )
            }
    }
}