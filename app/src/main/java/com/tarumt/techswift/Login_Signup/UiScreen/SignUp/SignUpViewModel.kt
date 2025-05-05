package com.tarumt.techswift.Login_Signup.UiScreen.SignUp

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.tarumt.techswift.Model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignUpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUIState())
    val uiState : StateFlow<SignUpUIState> = _uiState.asStateFlow()

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var name by mutableStateOf("")
        private set

    var phone by mutableStateOf("")
        private set

    var gender by mutableStateOf("Male")
        private set

    var address by mutableStateOf("")
        private set

    var postcode by mutableStateOf("")
        private set

    var state by mutableStateOf("")
        private set

    var role by mutableStateOf("User")
        private set


    //update var functions
    fun nameUpdate(it: String) {
        name = it
    }

    fun addressUpdate(it: String) {
        address = it
    }

    fun emailUpdate(it: String) {
        email = it
    }

    fun phoneUpdate(it: String) {
        phone = it
    }

    fun genderUpdate(it: String) {
        gender = it
    }

    fun postcodeUpdate(it: String) {
        postcode = it
    }

    fun stateUpdate(it: String) {
        state = it
    }
    fun passwordUpdate(it: String) {
        password = it
    }

    fun roleUpdate(it: String) {
        role = it
    }

    fun updateFullAdress(fullAddress: String) {
        _uiState.update { currentState ->
            currentState.copy(
                fullAddress = fullAddress
            )
        }
    }

    //update uistate
    fun updateProfileDetails():User {

        //update the current profile value.
        _uiState.update { currentState ->
            currentState.copy(
                currentProfile = User(
                    name = name,
                    email = email,
                    phone = phone,
                    gender = gender,
                    address1 = address,
                    postcode = postcode,
                    state = state,
                    fullAddress = _uiState.value.fullAddress,
                    role = role.substring(0,1)
                )
            )
        }
        return _uiState.value.currentProfile
    }

     fun resetValue() {
        _uiState.value = SignUpUIState()

        name = ""
        email = ""
        phone = ""
        gender = "Male"
        address = ""
        postcode = ""
        state = ""
        role = "User"
    }

    fun validateInputs(): Boolean {
        val emailValid =  android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val passwordValid = password.isNotBlank()
        val nameValid = name.isNotBlank()
        val malaysiaPhoneRegex = Regex("^01[0-46-9]\\d{7,8}$")
        val phoneValid = phone.matches(malaysiaPhoneRegex)
        val addressValid = address.isNotBlank()

        _uiState.update { currentState ->
            currentState.copy(
                textFieldError = listOf(
                    !emailValid,
                    !passwordValid,
                    !nameValid,
                    !phoneValid,
                    !addressValid
                )
            )
        }


        return _uiState.value.textFieldError.all { !it } //check if all are true, all true, return true
    }

    //Fetch and Pop suggestion from PlacesAPI(autocomplete) when users type address
    fun loadAddressSuggestion(context: Context){

        val placesClient = Places.createClient(context)
        val token = AutocompleteSessionToken.newInstance()

        if(address.length>3){
            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(address)
                .setCountries(listOf("MY"))
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    val suggestions = response.autocompletePredictions.map {
                        it.getFullText(null).toString()
                    }
                    _uiState.update { currentState->
                        currentState.copy(
                            addressSuggestion = suggestions
                        )
                    }
                }
                .addOnFailureListener {
                    Log.e("AddressAutocomplete", "Failed to load predictions", it)
                }
        }
        else{
            if(_uiState.value.addressSuggestion.isNotEmpty()){
                _uiState.update { currentState->
                    currentState.copy(
                        addressSuggestion  = emptyList()
                    )
                }
            }
        }
    }

    fun loadAndFillAddress(address : String,context: Context){
        val placeFields = listOf(Place.Field.ADDRESS_COMPONENTS,)

        val request = SearchByTextRequest
            .builder(address, placeFields)
            .setMaxResultCount(1)
            .build()

        val placesClient = Places.createClient(context) // make sure context is accessible
        placesClient.searchByText(request)
            .addOnSuccessListener { response ->
                val validPlace = response.places.firstOrNull { place ->
                    place.addressComponents?.asList()?.isNotEmpty() == true
                }

                validPlace?.addressComponents?.asList()?.let { addressComponents ->
                    // Process the address components and build a full address string
                    var fullAddress = ""
                    var postcode = ""
                    var state = ""
                    val builder = StringBuilder()
                    var foundLocality = false


                    for(component in addressComponents) {
                        if (!foundLocality) {
                            if (component.types.contains("locality")) {
                                foundLocality = true
                                continue // stop adding to fullAddress
                            }
                            builder.append("${component.name}, ")
                        }
                        if (component.types.contains("postal_code")) {
                            postcode = component.name
                        }

                        if (component.types.contains("locality") || component.types.contains("administrative_area_level_1") ) {
                            state = component.name
                        }
                    }

                    // Log the results
                    fullAddress = builder.toString().trim().removeSuffix(",")
                    addressUpdate(fullAddress)
                    postcodeUpdate(postcode)
                    stateUpdate(state)
                } ?: run {
                    Log.e("AddressAutocomplete", "No valid address found")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AddressAutocomplete", "Failed to load predictions", exception)
            }
    }
}