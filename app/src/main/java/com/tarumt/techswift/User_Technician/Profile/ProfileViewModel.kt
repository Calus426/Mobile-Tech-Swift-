    package com.tarumt.techswift.User_Technician.Profile
    
    import android.content.Context
    import android.util.Log
    import android.widget.Toast
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.setValue
    import androidx.lifecycle.ViewModel
    import com.google.android.libraries.places.api.Places
    import com.google.android.libraries.places.api.model.AutocompleteSessionToken
    import com.google.android.libraries.places.api.model.Place
    import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
    import com.google.android.libraries.places.api.net.SearchByTextRequest
    import com.google.firebase.Firebase
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.auth.FirebaseUser
    import com.google.firebase.firestore.firestore
    import com.tarumt.techswift.Model.User
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.flow.update
    
    class ProfileViewModel : ViewModel() {
    
       private val _uiState = MutableStateFlow(ProfileUiState())
        val uiState : StateFlow<ProfileUiState> = _uiState.asStateFlow()

        private val auth = FirebaseAuth.getInstance()
        private var previousUser: FirebaseUser? = null
        private var authStateListener: FirebaseAuth.AuthStateListener? = null
        
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

        val currentUser get() = auth.currentUser
    
    
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
    
    
    
        fun updateFullAdress(fullAddress: String ){
            _uiState.update { currentState ->
                currentState.copy(
                    fullAddress = fullAddress
                )
            }
        }
        fun updateProfileDetails(context: Context) {
    
            //update the current profile value.
            _uiState.update { currentState ->
                currentState.copy(
                    updatedProfile = User(
                        name = name,
                        email = email,
                        phone = phone,
                        gender = gender,
                        address1 = address,
                        postcode = postcode,
                        state = state,
                        fullAddress = _uiState.value.fullAddress
                    )
                )
            }
            //check the updated and ori different
            val updates = _uiState.value.oriProfile.getUpdatedFieldsMap(_uiState.value.updatedProfile)
    
            if (updates.isNotEmpty()) {
                currentUser?.let {
                    Firebase.firestore.collection("users")
                        .document(it.uid)
                        .update(updates)
                        .addOnSuccessListener {
                            //update the updated profile to ori and clear the updated for next time use
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
        }
    
        fun User.getUpdatedFieldsMap(updated: User): Map<String, Any> {
            return buildMap {
                if (updated.name.isNotEmpty() && updated.name != name) put("name", updated.name)
                if (updated.email.isNotEmpty() && updated.email != email) put("email", updated.email)
                if (updated.phone.isNotEmpty() && updated.phone != phone) put("phoneNumber", updated.phone)
                if (updated.gender.isNotEmpty() && updated.gender != gender) put("gender", updated.gender)
                if (updated.address1.isNotEmpty() && updated.address1 != address1) put("address1", updated.address1)
                if (updated.postcode != postcode) put("postcode", updated.postcode)
                if (updated.state.isNotEmpty() && updated.state != state) put("state", updated.state)
                if (updated.fullAddress.isNotEmpty() && updated.fullAddress != fullAddress) put("fullAddress", updated.fullAddress)
            }
        }
    
        fun getUserProfile(){
            currentUser?.let {
                Firebase.firestore.collection("users")
                    .document(it.uid)
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
                                address = it.address1
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
        }
    
        fun loadAddressSuggestion(context: Context){
    
            val placesClient = Places.createClient(context)
            val token = AutocompleteSessionToken.newInstance()
    
            if(address.length>3){
                val request = FindAutocompletePredictionsRequest.builder()
                    .setSessionToken(token)
                    .setQuery(address)
                    .setCountries(listOf("MY")) // 🔒 Restrict to Malaysia
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
    
    //                    addressComponents.forEach { component ->
    //                        when {
    //                            // Combine street number and route for the full address
    //                            component.types.contains("street_number") || component.types.contains("route")||component.types.contains("sublocality") -> {
    //                               if( component.types.contains("street_number") && component.name.isNotEmpty()){
    //                                   fullAddress += "${component.name}, "
    //                               }
    //                                else
    //                                fullAddress += "${component.name}, "
    //                            }
    //                            // Extract the postcode
    //                            component.types.contains("postal_code") -> {
    //                                postcode = component.name
    //                            }
    //                            // Extract state (administrative_area_level_1 or locality)
    //                            component.types.contains("locality") || component.types.contains("administrative_area_level_1") -> {
    //                                state = component.name
    //                            }
    //                        }
    //                    }
    
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
    
        fun resetProfile() {
            // Clear all mutable states
            name = ""
            email = ""
            phone = ""
            gender = ""
            address = ""
            postcode = ""
            state = ""
    
            // Reset UI state
            _uiState.update { ProfileUiState() }
    
            // Reload profile for new user
            getUserProfile()
        }


        private fun setupAuthListener() {
            authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                val currentUser = firebaseAuth.currentUser

                when {
                    // New user logged in or switched accounts
                    currentUser != null && currentUser.uid != previousUser?.uid -> {
                        previousUser = currentUser
                        resetProfile()
                    }

                    // No change (ignore)
                    else -> Unit
                }
            }
            auth.addAuthStateListener(authStateListener!!)
        }


        init{
            previousUser = auth.currentUser  // Initialize with current user
            setupAuthListener()
            getUserProfile()
        }
    
    
    
    }