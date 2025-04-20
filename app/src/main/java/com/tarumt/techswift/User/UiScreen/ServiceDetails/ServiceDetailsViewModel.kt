package com.tarumt.techswift.User.UiScreen.ServiceDetails

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.tarumt.techswift.Model.Request
import com.tarumt.techswift.Model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ServiceDetailsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ServiceDetailsUiState())
    val uiState : StateFlow<ServiceDetailsUiState> = _uiState.asStateFlow()
    var userDescription by mutableStateOf("")
        private set

    var offeredPrice by mutableStateOf("")
        private set

    var count by mutableStateOf(0)
        private set


    fun descriptionUpdate(description : String){
        userDescription = description
    }

    fun priceUpdate(price : String){
        offeredPrice = price
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

    fun updateOfferedPrice(){

        _uiState.update { currentState ->
            currentState.copy(
                offeredPrice = offeredPrice
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
        priceUpdate("")
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val request : Request = Request(
            id = count,
            serviceId =_uiState.value.serviceSelected.id ,
            textDescription = _uiState.value.textDescription,
            pictureDescription = _uiState.value.picturePath,
            createdTime = Timestamp.now(),
            offeredPrice = _uiState.value.offeredPrice.toDoubleOrNull(),
            userId = userId

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

            updateTextDescription()
            updateOfferedPrice()
            saveServiceRequest()
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
                        updateOfferedPrice()
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

    //validate
    fun validateInputs(): Boolean {
        val isDescriptionValid = userDescription.isNotBlank()
        val isPriceValid = offeredPrice.toDoubleOrNull()?.let { it > 0.0 } ?: false

        _uiState.update { currentState ->
            currentState.copy(
                descriptionError = !isDescriptionValid,
                priceError = !isPriceValid
            )
        }

        return isDescriptionValid && isPriceValid
    }

}