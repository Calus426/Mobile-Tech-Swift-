package com.tarumt.techswift.Technician

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tarumt.techswift.Model.Request
import com.tarumt.techswift.Model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TechnicianViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TechnicianUiState())
    val uiState: StateFlow<TechnicianUiState> = _uiState.asStateFlow()

    var fullAddress : String = ""
        private set
    private val db = FirebaseFirestore.getInstance()

    init {
        fetchPendingTasks()
    }

    fun fetchPendingTasks() {
        db.collection("requests")
            .whereEqualTo("pending", true)
            .get()
            .addOnSuccessListener { result ->
                val tasks = result.map { doc ->
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
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        pendingList = tasks
                    )

                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to fetch tasks")
            }
    }

    fun acceptTask(task: Request, context: Context) {
        val auth=FirebaseAuth.getInstance()
        val acceptedTimestamp = com.google.firebase.Timestamp.now()
        db.collection("requests").document("R"+task.id.toString())
            .update(
                mapOf(
                    "pending" to false,
                    "status" to "inProgress",
                    "technicianId" to auth.currentUser?.uid,
                    "acceptedTime" to acceptedTimestamp
                )
            )
            .addOnSuccessListener {
                fetchPendingTasks()
                Log.e("Firestore", "User ID is null, cannot fetch address")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to accept task")
            }
    }
    fun onTaskSelected(task: Request) {
        _uiState.update { it.copy(selectedTask = task, showDialog = true) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(showDialog = false, selectedTask = null) }
    }
    fun getUserAddress(userId: String) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    val fullAddr = user?.fullAddress ?: ""
                    val addr1 = user?.address1 ?: ""
                    if (fullAddr.isNotEmpty()) {
                        val info = UserAddressInfo(fullAddress = fullAddr, address1 = addr1)
                        _uiState.update { current ->
                            current.copy(
                                userAddresses = current.userAddresses + (userId to info)
                            )
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to fetch user address")
            }
    }
}