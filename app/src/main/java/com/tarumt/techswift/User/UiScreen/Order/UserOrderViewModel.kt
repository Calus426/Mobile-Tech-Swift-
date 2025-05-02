package com.tarumt.techswift.User.UiScreen.Order

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

class UserOrderViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UserOrderUiState())
    val uiState: StateFlow<UserOrderUiState> = _uiState.asStateFlow()
    private var pendingListener: ListenerRegistration? = null
    private var inProgressListener: ListenerRegistration? = null

    private var currentUserId: String? = null

    private var technicianName : String = ""
    private var technicianPhone : String = ""

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
        pendingListener?.remove()
        inProgressListener?.remove()

        // Reset state
        _uiState.value = UserOrderUiState()

        // Reload data for new user
        loadPendingRequest()
        loadInProgressRequest()
        Log.d("HistoryVM", "Reloaded history for new user")
    }

    fun clearHistory() {
        pendingListener?.remove()
        inProgressListener?.remove()
        _uiState.value = UserOrderUiState()
    }

    fun statusScreenUpdate(screen : String){
        _uiState.update { currentState->
            currentState.copy(
                statusScreen = screen
            )
        }
    }

    fun loadPendingRequest() {
        currentUserId?.let { userId ->
            val db = Firebase.firestore
            val collectionRef = db.collection("requests")

            pendingListener?.remove()

            pendingListener = collectionRef
                .whereEqualTo("userId", userId)
                .whereEqualTo("pending", true)
                .orderBy("createdTime", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        Log.e("Firestore", "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        val pendingRequests = querySnapshot.toObjects(Request::class.java)
                        updatePendingRequestList(pendingRequests)
                    } else {

                        updatePendingRequestList(emptyList<Request>().toMutableList())
                    }

                }
        }


    }

    fun loadInProgressRequest() {
        currentUserId?.let { userId ->
            val db = Firebase.firestore
            val collectionRef = db.collection("requests")

            inProgressListener?.remove()

            inProgressListener = collectionRef
                .whereEqualTo("userId", userId)
                .whereEqualTo("pending", false)
                .whereEqualTo("status", "inProgress")
                .orderBy("createdTime", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        Log.e("Firestore", "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        val inProgressRequest = querySnapshot.toObjects(Request::class.java)

                        // For each request, fetch technician info and create DTO
                        val dtoList = mutableListOf<RequestDTO>()
                        val totalRequests = inProgressRequest.size
                        var processedCount = 0

                        inProgressRequest.forEach { request ->
                            Firebase.firestore.collection("users")
                                .document(request.technicianId)
                                .get()
                                .addOnSuccessListener { document ->
                                    val technicianName = document.getString("name") ?: ""
                                    val technicianPhone = document.getString("phone") ?: ""
                                    dtoList.add(RequestDTO(request, technicianName, technicianPhone))
                                }
                                .addOnCompleteListener {
                                    processedCount++
                                    if (processedCount == totalRequests) {
                                        updateInProgressRequestList(dtoList)
                                    }
                                }
                        }
                    } else {

                        updateInProgressRequestList(emptyList<RequestDTO>().toMutableList())
                    }

                }
        }
    }


    fun updatePendingRequestList(pendingRequests: MutableList<Request>) {
        _uiState.update { currentState ->
            currentState.copy(pendingList = pendingRequests.toMutableList())
        }

    }

    fun updateInProgressRequestList(inProgressRequest: MutableList<RequestDTO>) {
        _uiState.update { currentState ->
            currentState.copy(inProgressList = inProgressRequest.toMutableList())
        }

    }

    fun clearToastMessage() {
        _uiState.update { currentState ->
            currentState.copy(toastMessage = "")
        }
    }

    fun updateToastMessage(text: String) {
        _uiState.update { currentState ->
            currentState.copy(toastMessage = text)
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
//                        _uiState.update { currentState ->
//                            currentState.copy(
//                                technicianName = it.name,
//                                technicianPhone = it.phone
//                            )
//                        }
                        technicianName = it.name
                        technicianPhone = it.phone

                    }
                } else {
                    Log.d("Firestore", "No such document")
                }

            }.addOnFailureListener { exception ->
                Log.e("Firestore", "Failed to fetch user", exception)
            }
    }
    fun getTechnicianName() : String{
        return technicianName
    }
    fun getTechnicianPhone() : String{
        return technicianPhone
    }
}

