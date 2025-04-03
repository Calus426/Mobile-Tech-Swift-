package com.tarumt.techswift.User.UiScreen.ServiceDetails

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.tarumt.techswift.User.Model.Request
import com.tarumt.techswift.User.Model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ServiceDetailsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ServiceDetailsUiState())
    val uiState : StateFlow<ServiceDetailsUiState> = _uiState.asStateFlow()
    var userDescription by mutableStateOf("")
        private set




    fun descriptionUpdate(description : String){
        userDescription = description
    }

    fun updateServiceId(service : Service){

        _uiState.update { currentState ->
            currentState.copy(
                serviceSelected = service
            )
        }
    }

    fun updatePictureDescription(uri : Uri){
        _uiState.update { currentState ->
            currentState.copy(
                pictureDescription = uri
            )
        }
    }

    fun updateTextDescription(){

        _uiState.update { currentState ->
            currentState.copy(
                textDescription = userDescription
            )
        }
    }

    fun resetServiceDetails(){
        descriptionUpdate("")
        _uiState.value = ServiceDetailsUiState()
    }

    fun saveServiceRequest(){

        val request : Request = Request(
            id = 1,
            serviceId =_uiState.value.serviceSelected.id ,
            textDescription = _uiState.value.textDescription,
            userId = 0
        )
        val dbRef = Firebase.firestore
            .collection("requests")
            .document("R3")
        try{
            dbRef
                .set(request)
                .addOnSuccessListener {
                    Log.d("AddRequest","Successfully added")
                }

        }catch(e:Exception){
            Log.d("AddRequest" ,e.toString())
        }
    }



}