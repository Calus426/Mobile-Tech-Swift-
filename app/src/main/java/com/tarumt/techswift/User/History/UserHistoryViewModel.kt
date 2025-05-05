package com.tarumt.techswift.User.History

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.tarumt.techswift.Model.Request
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserHistoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UserHistoryUiState())
    val uiState: StateFlow<UserHistoryUiState> = _uiState.asStateFlow()
    private var finishedListener: ListenerRegistration? = null

    private var currentUserId: String? = null

    init {
        setupAuthListener()
    }

    private fun setupAuthListener() {
        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            firebaseAuth.currentUser?.let { user ->
                if (user.uid != currentUserId) {
                    // User changed - reload everything
                    currentUserId = user.uid
                    resetAndReloadHistory()
                }
            } ?: run {
                // User logged out - clear everything
                currentUserId = null
                clearHistory()
            }
        }
    }

    fun resetAndReloadHistory() {
        // Clear existing listeners
        finishedListener?.remove()

        // Reset state
        _uiState.value = UserHistoryUiState()

        // Reload data for new user
        loadFinishedList()
        Log.d("HistoryVM", "Reloaded history for new user")
    }

    fun clearHistory() {
        finishedListener?.remove()
        _uiState.value = UserHistoryUiState()
    }

    fun loadFinishedList() { //Load finished request

        currentUserId?.let { userId ->
            val db = Firebase.firestore
            val collectionRef = db.collection("requests")

            finishedListener?.remove()

            finishedListener = collectionRef
                .whereEqualTo("userId", userId)
                .whereEqualTo("pending", false)
                .whereEqualTo("status", "finished")
                .orderBy("createdTime", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        Log.e("Firestore", "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        val pendingRequests = querySnapshot.toObjects(Request::class.java)

                        // For each request, fetch technician info and create DTO
                        val dtoList = mutableListOf<RequestDTO>()
                        val totalRequests = pendingRequests.size
                        var processedCount = 0

                        pendingRequests.forEach { request ->
                            Firebase.firestore.collection("users")
                                .document(request.technicianId)
                                .get()
                                .addOnSuccessListener { document ->
                                    val technicianName = document.getString("name") ?: ""
                                    dtoList.add(RequestDTO(request, technicianName))
                                }
                                .addOnCompleteListener {
                                    processedCount++
                                    if (processedCount == totalRequests) {
                                        updateFinishedRequestList(dtoList)
                                    }
                                }
                        }
                    } else {
                        updateFinishedRequestList(emptyList<RequestDTO>().toMutableList())
                    }

                }
        }

    }
    fun updateFinishedRequestList(finishedList: MutableList<RequestDTO>) {
        _uiState.update { currentState ->
            currentState.copy(finishedList = finishedList.toMutableList())
        }

    }


}