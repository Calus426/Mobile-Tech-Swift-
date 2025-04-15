package com.tarumt.techswift.User.UiScreen.Profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.tarumt.techswift.User.Model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

   private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState : StateFlow<ProfileUiState> = _uiState.asStateFlow()

    var name by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var phone by mutableStateOf("")
        private set

    var gender by mutableStateOf("")
        private set

    var address by mutableStateOf("")
        private set

    var postcode by mutableStateOf("")
        private set

    var state by mutableStateOf("")
        private set


    fun nameUpdate(it : String){
        name = it
    }

    fun addressUpdate(it: String){
        address = it
    }

    fun emailUpdate(it: String){
        email = it
    }

    fun phoneUpdate(it: String){
        phone = it
    }

    fun genderUpdate(it: String){
        gender = it
    }

    fun postcodeUpdate(it: String){
        postcode = it
    }

    fun stateUpdate(it: String){
        state = it
    }

    fun updateProfileDetails(context: Context) {
        _uiState.update { currentState ->
            currentState.copy(
                updatedProfile = User(
                    name = name,
                    email = email,
                    phone = phone,
                    gender = gender,
                    address = address,
                    postcode = postcode,
                    state = state

                )
            )
        }
        val updates = _uiState.value.oriProfile.getUpdatedFieldsMap(_uiState.value.updatedProfile)

        if (updates.isNotEmpty()) {
            Firebase.firestore.collection("users")
                .document("ugJPJBqAV1f9EdJk5MWOBpvrLxY2")
                .update(updates)
                .addOnSuccessListener {
                  _uiState.update { currentState ->
                      currentState.copy(
                          oriProfile = _uiState.value.updatedProfile,
                          updatedProfile = User()
                      )
                  }

                    Toast.makeText(context,"Save Profile Sucessfully!",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    // Handle error
                }
        }
    }

    fun User.getUpdatedFieldsMap(updated: User): Map<String, Any> {
        return buildMap {
            if (updated.name.isNotEmpty() && updated.name != name) put("name", updated.name)
            if (updated.email.isNotEmpty() && updated.email != email) put("email", updated.email)
            if (updated.phone.isNotEmpty() && updated.phone != phone) put("phoneNumber", updated.phone)
            if (updated.gender.isNotEmpty() && updated.gender != gender) put("gender", updated.gender)
            if (updated.address.isNotEmpty() && updated.address != address) put("address", updated.address)
            if (updated.postcode.isNotEmpty() && updated.postcode != postcode) put("postcode", updated.postcode)
            if (updated.state.isNotEmpty() && updated.state != state) put("state", updated.state)
        }
    }

    fun loadUserProfile(){
        Firebase.firestore.collection("users")
            .document("ugJPJBqAV1f9EdJk5MWOBpvrLxY2")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    user?.let {
                        // Set oriProfile to this user
                        _uiState.update { currentState ->
                            currentState.copy(
                                oriProfile = it
                            )
                        }
                        name = it.name
                        email = it.email
                        phone = it.phone
                        gender = it.gender
                        address = it.address
                        postcode = it.postcode
                        state = it.state
                    }
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Failed to fetch user", exception)
            }
    }

    init{
        loadUserProfile()
    }
}