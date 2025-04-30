package com.tarumt.techswift.Technician.TechnicianHistory

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.tarumt.techswift.Model.Request
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
                val inProgress = mutableListOf<Request>()
                val finished = mutableListOf<Request>()

                result.forEach { doc ->
                    val task = doc.toObject(Request::class.java)
                    val uiTask =
                        Request(

                        id = doc.getLong("id")?.toInt() ?: 0,
                        serviceId = doc.getLong("serviceId")?.toInt() ?: 0,
                        textDescription = doc.getString("textDescription") ?: "",
                        pictureDescription = doc.getString("pictureDescription") ?: "",
                        userId = doc.getString("userId") ?: "",
                        pending = doc.getBoolean("pending") ?: true,
                        technicianId = doc.getString("technicianId") ?: "",
                        createdTime =doc.getTimestamp("createdTime") ?:null,
                        offeredPrice = doc.getDouble("offeredPrice"),
                        acceptedTime = doc.getTimestamp("acceptedTime"),
                        finishedTime = doc.getTimestamp("finishedTime")


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
        val finishedTimestamp = com.google.firebase.Timestamp.now()
        db.collection("requests")
            .document(taskId)
            .update(
                mapOf(
                    "status" to "finished",
                    "finishedTime" to finishedTimestamp
                )
            )
            .addOnSuccessListener {
                fetchHistory() // Refresh to get updated time
            }
            .addOnFailureListener { e ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error updating task"
                )
            }
    }

}