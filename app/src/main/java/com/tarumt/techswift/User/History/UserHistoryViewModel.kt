package com.tarumt.techswift.User.History

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.tarumt.techswift.Model.Request
import com.tarumt.techswift.Model.User
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

    fun loadFinishedList() {

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
                        updateFinishedRequestList(pendingRequests)
                    } else {

                        updateFinishedRequestList(emptyList<Request>().toMutableList())
                    }

                }
        }

    }

    fun updateFinishedRequestList(finishedList : MutableList<Request>){
        _uiState.update { currentState ->
            currentState.copy(
                finishedList = finishedList
            )
        }
    }

    fun getTechnicianDetails(technicianId: String){

        Firebase.firestore.collection("users")
            .document(technicianId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    user?.let {
                        _uiState.update { currentState ->
                            currentState.copy(
                                technicianName = it.name,
                            )
                        }

                    }
                } else {
                    Log.d("Firestore", "No such document")
                }

            }.addOnFailureListener { exception ->
                Log.e("Firestore", "Failed to fetch user", exception)
            }
    }
}