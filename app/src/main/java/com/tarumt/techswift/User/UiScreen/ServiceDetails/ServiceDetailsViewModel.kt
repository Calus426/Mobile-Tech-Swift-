package com.tarumt.techswift.User.UiScreen.ServiceDetails

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.tarumt.techswift.User.Model.Request
import com.tarumt.techswift.User.Model.Service
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ServiceDetailsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ServiceDetailsUiState())
    val uiState : StateFlow<ServiceDetailsUiState> = _uiState.asStateFlow()
    var userDescription by mutableStateOf("")
        private set

    var count by mutableStateOf(0)
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

    fun updatePicturePath(path : String){
        _uiState.update { currentState ->
            currentState.copy(
                picturePath = path
            )
        }
    }

    fun resetServiceDetails(){
        descriptionUpdate("")
        _uiState.value = ServiceDetailsUiState()
    }

    private fun loadCount() {
        val db = Firebase.firestore
        val collectionRef = db.collection("requests")

        collectionRef.count().get(AggregateSource.SERVER)
            .addOnSuccessListener { snapshot ->
                count = snapshot.count.toInt()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting count", e)
                // You might want to set count = 0 here as fallback
            }
    }

    fun saveServiceRequest(){
        count ++
        val request : Request = Request(
            id = count,
            serviceId =_uiState.value.serviceSelected.id ,
            textDescription = _uiState.value.textDescription,
            pictureDescription = _uiState.value.picturePath,
            createdTime = Timestamp.now()

        )
        val dbRef = Firebase.firestore
            .collection("requests")
            .document("R$count")
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

    fun savePictureDescription(context: Context,onComplete:()-> Unit) {
        val uri = _uiState.value.pictureDescription ?: run {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            onComplete()
            return
        }

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val path = "images/"+ uri.lastPathSegment //images/blabla.jpg
        val imageRef = storageRef.child(path)

        val uploadTask = imageRef.putFile(uri)

        uploadTask
            .addOnSuccessListener {
                imageRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        val url = downloadUri.toString()
                         // optional, if you need to observe it
                        updatePicturePath(url)
                        updateTextDescription()
                        saveServiceRequest()
                        onComplete()
                    }

        }
            .addOnFailureListener {
                onComplete()
                Toast.makeText(context, "Fail upload picture", Toast.LENGTH_SHORT).show()
            }



    }
    init{
        loadCount()
    }


}