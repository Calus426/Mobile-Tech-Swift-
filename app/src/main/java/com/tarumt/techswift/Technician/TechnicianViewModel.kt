package com.tarumt.techswift.Technician

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
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
                        offeredPrice = doc.getDouble("offeredPrice")

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
        db.collection("requests").document("R"+task.id.toString())
            .update(
                mapOf(
                    "pending" to false,
                    "status" to "inProgress",
                    "technicianId" to auth.currentUser?.uid
                )
            )
            .addOnSuccessListener {
                fetchPendingTasks()
                task.userId?.let { userId ->
                    getUserAddress(userId, context)
                } ?: Log.e("Firestore", "User ID is null, cannot fetch address")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to accept task")
            }
    }

    private fun openMapForNavigation(context: Context, address: String) {
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

        val chooser = Intent.createChooser(mapIntent, "Open with")
        chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(chooser)
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
                    } else {
                        Log.e("MapError", "User address is null or empty")
                    }
                } else {
                    Log.e("Firestore", "User document does not exist")
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to fetch user address")
            }
    }
}