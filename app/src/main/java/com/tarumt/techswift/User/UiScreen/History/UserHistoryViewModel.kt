package com.tarumt.techswift.User.UiScreen.History

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.tarumt.techswift.User.Model.Request
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserHistoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UserHistoryUiState())
    val uiState: StateFlow<UserHistoryUiState> = _uiState.asStateFlow()

    fun resetHistory() {
        _uiState.value = UserHistoryUiState()
    }

    init {
        resetHistory()
        loadPendingRequest()
    }

    fun loadPendingRequest() {
        val db = Firebase.firestore
        val collectionRef = db.collection("requests")

        collectionRef.whereEqualTo("pending", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.d("Firestore", "No pending requests found.")
                } else {
                    val pendingRequests = querySnapshot.toObjects(Request::class.java)
                    updatePendingRequestList(pendingRequests)

                }
            }
    }

    fun updatePendingRequestList(pendingRequests: MutableList<Request>) {
        _uiState.update { currentState ->
            currentState.copy(pendingList = pendingRequests.toMutableList())
        }

    }
}

