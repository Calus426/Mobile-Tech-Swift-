package com.tarumt.techswift.Technician.TechnicianHistory

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tarumt.techswift.Model.User
import com.tarumt.techswift.Model.Request
import com.tarumt.techswift.Technician.TechnicianUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TechnicianHistoryViewModel : ViewModel() {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    private val _uiState = MutableStateFlow(TechnicianHistoryUiState())
    val uiState: StateFlow<TechnicianHistoryUiState> = _uiState.asStateFlow()
    private val _userPhones = mutableStateMapOf<String, String>()
    val userPhones: Map<String, String> get() = _userPhones

    private val db = FirebaseFirestore.getInstance()

    fun fetchHistory() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        db.collection("requests")
            .whereEqualTo("pending", false,)// Only accepted tasks
            .whereEqualTo("technicianId",currentUserId)
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
    fun getUserPhone(userId: String, onPhoneFetched: (String) -> Unit) {
        if (_userPhones.containsKey(userId)) {
            onPhoneFetched(_userPhones[userId]!!)
            return
        }

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    val phone = user?.phone
                    if (!phone.isNullOrEmpty()) {
                        _userPhones[userId] = phone
                        onPhoneFetched(phone)
                    }
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to fetch user phone")
            }
    }

    fun getUserAddress(userId: String, context: Context) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    val address = user?.fullAddress
                    if (!address.isNullOrEmpty()) {
                        openMapForNavigation(context, address)
                    }
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to fetch user address")
            }
    }

    private fun openMapForNavigation(context: Context, address: String) {
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        val chooser = Intent.createChooser(mapIntent, "Open with")
        chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(chooser)
    }


}