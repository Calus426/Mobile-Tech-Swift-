package com.tarumt.techswift.User.UiScreen.History

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.tarumt.techswift.User.Model.Request
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserHistoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UserHistoryUiState())
    val uiState: StateFlow<UserHistoryUiState> = _uiState.asStateFlow()
    private var snapshotListener: ListenerRegistration? = null

    fun resetHistory() {
        _uiState.value = UserHistoryUiState()
    }

    init {
        resetHistory()
        loadPendingRequest()
        Log.d("Init","Initial")
    }



    fun loadPendingRequest() {
        val db = Firebase.firestore
        val collectionRef = db.collection("requests")

        snapshotListener?.remove()

        snapshotListener = collectionRef
            .whereEqualTo("pending", true)
            .orderBy("createdTime",Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (querySnapshot != null && !querySnapshot.isEmpty) {
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

    fun clearToastMessage() {
        _uiState.update { currentState ->
            currentState.copy(toastMessage = "")
        }
    }

    fun updateToastMessage(text : String) {
        _uiState.update { currentState ->
            currentState.copy(toastMessage = text)
        }
    }

    init{
        loadPendingRequest()
    }
}

